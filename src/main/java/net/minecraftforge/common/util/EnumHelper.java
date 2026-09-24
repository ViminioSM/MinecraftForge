package net.minecraftforge.common.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.*;

import sun.misc.Unsafe;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity.EnumEntitySize;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Stronghold.Door;
import net.minecraftforge.classloading.FMLForgePlugin;

public class EnumHelper
{
    private static Unsafe unsafe                 = null;
    // Legacy (Java 8 and earlier) enum construction route, kept as a fallback.
    private static Object legacyReflectionFactory      = null;
    private static Method legacyNewConstructorAccessor = null;
    private static Method legacyNewInstance            = null;
    private static boolean isSetup               = false;

    //Some enums are decompiled with extra arguments, so lets check for that
    @SuppressWarnings("rawtypes")
    private static Class[][] commonTypes =
    {
        {EnumAction.class},
        {ArmorMaterial.class, int.class, int[].class, int.class},
        {EnumArt.class, String.class, int.class, int.class, int.class, int.class},
        {EnumCreatureAttribute.class},
        {EnumCreatureType.class, Class.class, int.class, Material.class, boolean.class, boolean.class},
        {Door.class},
        {EnumEnchantmentType.class},
        {EnumEntitySize.class},
        {Sensitivity.class},
        {MovingObjectType.class},
        {EnumSkyBlock.class, int.class},
        {EnumStatus.class},
        {ToolMaterial.class, int.class, int.class, float.class, float.class, int.class},
        {EnumRarity.class, EnumChatFormatting.class, String.class}
    };

    public static EnumAction addAction(String name)
    {
        return addEnum(EnumAction.class, name);
    }
    public static ArmorMaterial addArmorMaterial(String name, int durability, int[] reductionAmounts, int enchantability)
    {
        return addEnum(ArmorMaterial.class, name, durability, reductionAmounts, enchantability);
    }
    public static EnumArt addArt(String name, String tile, int sizeX, int sizeY, int offsetX, int offsetY)
    {
        return addEnum(EnumArt.class, name, tile, sizeX, sizeY, offsetX, offsetY);
    }
    public static EnumCreatureAttribute addCreatureAttribute(String name)
    {
        return addEnum(EnumCreatureAttribute.class, name);
    }
    @SuppressWarnings("rawtypes")
    public static EnumCreatureType addCreatureType(String name, Class typeClass, int maxNumber, Material material, boolean peaceful, boolean animal)
    {
        return addEnum(EnumCreatureType.class, name, typeClass, maxNumber, material, peaceful, animal);
    }
    public static Door addDoor(String name)
    {
        return addEnum(Door.class, name);
    }
    public static EnumEnchantmentType addEnchantmentType(String name)
    {
        return addEnum(EnumEnchantmentType.class, name);
    }
    public static EnumEntitySize addEntitySize(String name)
    {
        return addEnum(EnumEntitySize.class, name);
    }
    public static Sensitivity addSensitivity(String name)
    {
        return addEnum(Sensitivity.class, name);
    }
    public static MovingObjectType addMovingObjectType(String name)
    {
        return addEnum(MovingObjectType.class, name);
    }
    public static EnumSkyBlock addSkyBlock(String name, int lightValue)
    {
        return addEnum(EnumSkyBlock.class, name, lightValue);
    }
    public static EnumStatus addStatus(String name)
    {
        return addEnum(EnumStatus.class, name);
    }
    public static ToolMaterial addToolMaterial(String name, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability)
    {
        return addEnum(ToolMaterial.class, name, harvestLevel, maxUses, efficiency, damage, enchantability);
    }
    public static EnumRarity addRarity(String name, EnumChatFormatting color, String displayName)
    {
        return addEnum(EnumRarity.class, name, color, displayName);
    }

    private static void setup()
    {
        if (isSetup)
        {
            return;
        }

        try
        {
            // sun.misc.Unsafe lives in the jdk.unsupported module, which is
            // present and exported on every relevant runtime (8 through 21+).
            // It replaces the removed sun.reflect internals previously used
            // here (Field.modifiers, ReflectionFactory accessors).
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            // Legacy construction route, only present on Java 8 and earlier.
            Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
            legacyReflectionFactory      = getReflectionFactory.invoke(null);
            legacyNewConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
            legacyNewInstance            = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
        }
        catch (Exception e)
        {
            // Expected on Java 9+: the legacy route is simply unavailable there.
            legacyReflectionFactory = null;
        }

        isSetup = true;
    }

    /*
     * Enum instances are created through MethodHandles: unlike
     * Constructor.newInstance, a MethodHandle to the enum constructor does not
     * refuse to create enum objects, and it invokes the real constructor, so
     * custom enum fields are initialized exactly as on Java 8.
     * (Technique verified on runtimes 9 through 21. MethodHandles.Lookup and
     * MethodType exist since Java 7, so this compiles for 1.8 targets; only
     * privateLookupIn itself is looked up reflectively.)
     */
    private static < T extends Enum<? >> T makeEnum(Class<T> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception
    {
        Class<?>[] parameterTypes = new Class[additionalTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalTypes, 0, parameterTypes, 2, additionalTypes.length);
        Object[] parms = new Object[additionalValues.length + 2];
        parms[0] = value;
        parms[1] = Integer.valueOf(ordinal);
        System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
        try
        {
            Method privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
            Object lookup = privateLookupIn.invoke(null, enumClass, MethodHandles.lookup());
            MethodType constructorType = MethodType.methodType(void.class, parameterTypes);
            MethodHandle constructor = (MethodHandle) lookup.getClass().getMethod("findConstructor", Class.class, MethodType.class).invoke(lookup, enumClass, constructorType);
            try
            {
                return enumClass.cast(constructor.invokeWithArguments(Arrays.asList(parms)));
            }
            catch (Throwable t)
            {
                if (t instanceof Exception)
                {
                    throw (Exception) t;
                }
                if (t instanceof Error)
                {
                    throw (Error) t;
                }
                throw new RuntimeException(t);
            }
        }
        catch (NoSuchMethodException e)
        {
            // Java 8 and earlier: fall back to the legacy ConstructorAccessor route.
            return enumClass.cast(makeEnumLegacy(enumClass, parameterTypes, parms));
        }
    }

    private static Object makeEnumLegacy(Class<?> enumClass, Class<?>[] parameterTypes, Object[] parms) throws Exception
    {
        if (legacyReflectionFactory == null)
        {
            throw new IllegalStateException("No enum construction route available on this runtime");
        }
        Object accessor = legacyNewConstructorAccessor.invoke(legacyReflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
        return legacyNewInstance.invoke(accessor, new Object[] { parms });
    }

    public static void setFailsafeFieldValue(Field field, Object target, Object value) throws Exception
    {
        // Writes through Unsafe, bypassing final checks and module access
        // checks alike. Used for the $VALUES array and the enum caches in
        // java.lang.Class, which are inaccessible via setAccessible on
        // modern runtimes.
        Class<?> type = field.getType();
        if (Modifier.isStatic(field.getModifiers()))
        {
            Object base = unsafe.staticFieldBase(field);
            long offset = unsafe.staticFieldOffset(field);
            putValue(base, offset, type, value);
        }
        else
        {
            long offset = unsafe.objectFieldOffset(field);
            putValue(target, offset, type, value);
        }
    }

    private static void putValue(Object base, long offset, Class<?> type, Object value)
    {
        if (!type.isPrimitive())
        {
            unsafe.putObject(base, offset, value);
        }
        else if (type == int.class)
        {
            unsafe.putInt(base, offset, ((Number) value).intValue());
        }
        else if (type == long.class)
        {
            unsafe.putLong(base, offset, ((Number) value).longValue());
        }
        else if (type == boolean.class)
        {
            unsafe.putBoolean(base, offset, ((Boolean) value).booleanValue());
        }
        else if (type == float.class)
        {
            unsafe.putFloat(base, offset, ((Number) value).floatValue());
        }
        else if (type == double.class)
        {
            unsafe.putDouble(base, offset, ((Number) value).doubleValue());
        }
        else if (type == short.class)
        {
            unsafe.putShort(base, offset, ((Number) value).shortValue());
        }
        else if (type == byte.class)
        {
            unsafe.putByte(base, offset, ((Number) value).byteValue());
        }
        else if (type == char.class)
        {
            unsafe.putChar(base, offset, ((Character) value).charValue());
        }
        else
        {
            throw new IllegalArgumentException("Unsupported field type " + type);
        }
    }

    private static void blankField(Class<?> enumClass, String fieldName) throws Exception
    {
        for (Field field : Class.class.getDeclaredFields())
        {
            if (field.getName().contains(fieldName))
            {
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }

    private static void cleanEnumCache(Class<?> enumClass) throws Exception
    {
        blankField(enumClass, "enumConstantDirectory");
        blankField(enumClass, "enumConstants");
    }

    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
    {
        setup();
        return addEnum(commonTypes, enumType, enumName, paramValues);
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Enum<? >> T addEnum(Class[][] map, Class<T> enumType, String enumName, Object... paramValues)
    {
        for (Class[] lookup : map)
        {
            if (lookup[0] == enumType)
            {
                Class<?>[] paramTypes = new Class<?>[lookup.length - 1];
                if (paramTypes.length > 0)
                {
                    System.arraycopy(lookup, 1, paramTypes, 0, paramTypes.length);
                }
                return addEnum(enumType, enumName, paramTypes, paramValues);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes, Object[] paramValues)
    {
        if (!isSetup)
        {
            setup();
        }

        Field valuesField = null;
        Field[] fields = enumType.getDeclaredFields();

        for (Field field : fields)
        {
            String name = field.getName();
            if (name.equals("$VALUES") || name.equals("ENUM$VALUES")) //Added 'ENUM$VALUES' because Eclipse's internal compiler doesn't follow standards
            {
                valuesField = field;
                break;
            }
        }

        int flags = (FMLForgePlugin.RUNTIME_DEOBF ? Modifier.PUBLIC : Modifier.PRIVATE) | Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
        if (valuesField == null)
        {
            String valueType = String.format("[L%s;", enumType.getName().replace('.', '/'));

            for (Field field : fields)
            {
                if ((field.getModifiers() & flags) == flags &&
                     field.getType().getName().replace('.', '/').equals(valueType)) //Apparently some JVMs return .'s and some don't..
                {
                    valuesField = field;
                    break;
                }
            }
        }

        if (valuesField == null)
        {
            FMLLog.severe("Could not find $VALUES field for enum: %s", enumType.getName());
            FMLLog.severe("Runtime Deobf: %s", FMLForgePlugin.RUNTIME_DEOBF);
            FMLLog.severe("Flags: %s", String.format("%16s", Integer.toBinaryString(flags)).replace(' ', '0'));
            FMLLog.severe("Fields:");
            for (Field field : fields)
            {
                String mods = String.format("%16s", Integer.toBinaryString(field.getModifiers())).replace(' ', '0');
                FMLLog.severe("       %s %s: %s", mods, field.getName(), field.getType().getName());
            }
            return null;
        }

        valuesField.setAccessible(true);

        try
        {
            T[] previousValues = (T[])valuesField.get(enumType);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            T newValue = (T)makeEnum(enumType, enumName, values.size(), paramTypes, paramValues);
            values.add(newValue);
            setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));
            cleanEnumCache(enumType);

            return newValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static
    {
        if (!isSetup)
        {
            setup();
        }
    }
}