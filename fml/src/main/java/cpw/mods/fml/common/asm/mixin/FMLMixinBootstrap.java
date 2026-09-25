/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.asm.mixin;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * Bootstraps native Mixin support (SpongePowered Mixin, MIT licensed,
 * external dependency) into FML.
 *
 * <p>All interaction with Mixin classes is reflective, so FML starts
 * normally when the Mixin jar is absent - only Mixin support is skipped.
 * Coremods derived from the MixinBooter/UniMixins orchestration idiom
 * (early queuing in coremod plugins, late refresh around mod construction)
 * work against the equivalent hooks here; see {@link FMLMixinController}.</p>
 */
public class FMLMixinBootstrap
{
    public static final String MIXIN_BOOTSTRAP_CLASS = "org.spongepowered.asm.launch.MixinBootstrap";
    public static final String MIXIN_TWEAKER_CLASS = "org.spongepowered.asm.launch.MixinTweaker";
    public static final String MIXIN_PROXY_CLASS = "org.spongepowered.asm.mixin.transformer.Proxy";

    private FMLMixinBootstrap()
    {
    }

    /**
     * The classloader that owns the single Mixin runtime. Mixin classes are
     * resolved through it everywhere so all glue code talks to the same
     * runtime no matter which classloader defined the calling FML class
     * (tweaker-phase classes may live in a different loader than game-phase
     * classes on modern runtimes).
     */
    public static ClassLoader mixinLoader()
    {
        return Launch.class.getClassLoader();
    }

    /**
     * Mixin state is kept on the Launch blackboard instead of in statics so
     * it is shared no matter which classloader defined the calling FML glue
     * class (see {@link #mixinLoader}).
     */
    private static boolean readFlag()
    {
        try
        {
            return Boolean.TRUE.equals(Launch.blackboard.get("fml.mixin.initialised"));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static void writeFlag()
    {
        try
        {
            Launch.blackboard.put("fml.mixin.initialised", Boolean.TRUE);
        }
        catch (Exception e)
        {
            // blackboard unavailable - Mixin support simply stays disabled
        }
    }

    /**
     * @return true when Mixin is present and initialised, false when Mixin
     *         support was skipped (disabled flag or jar absent).
     */
    public static boolean init(LaunchClassLoader classLoader)
    {
        if (readFlag())
        {
            return true;
        }
        if (Boolean.parseBoolean(System.getProperty("fml.mixin.disable", "false")))
        {
            FMLRelaunchLog.info("Mixin support disabled via -Dfml.mixin.disable=true");
            return false;
        }
        try
        {
            Class.forName(MIXIN_BOOTSTRAP_CLASS, true, mixinLoader());
        }
        catch (ClassNotFoundException e)
        {
            FMLRelaunchLog.info("SpongePowered Mixin not found on the classpath - Mixin support skipped");
            return false;
        }
        try
        {
            classLoader.addTransformerExclusion("org.spongepowered.");
            invokeStatic(MIXIN_BOOTSTRAP_CLASS, "init");
            // Production runtime names are searge-mapped; mods author mixins
            // against searge names and ship refmaps for notch.
            setObfuscationContext("searge");
            registerErrorHandler();
            writeFlag();
            refreshTransformerExclusions(classLoader);
            FMLRelaunchLog.info("SpongePowered Mixin support initialised");
            return true;
        }
        catch (Exception e)
        {
            FMLRelaunchLog.warning("Mixin bootstrap failed - continuing without Mixin support: %s", e.toString());
            return false;
        }
    }

    public static boolean isInitialised()
    {
        return readFlag();
    }

    /** True when the Mixin transformer is already registered (e.g. an external booter owns Mixin). */
    public static boolean isProxyRegistered(LaunchClassLoader classLoader)
    {
        for (Object transformer : classLoader.getTransformers())
        {
            if (transformer != null && MIXIN_PROXY_CLASS.equals(transformer.getClass().getName()))
            {
                return true;
            }
        }
        return false;
    }

    static Object invokeStatic(String className, String method, Object... args) throws Exception
    {
        Class<?> clazz = Class.forName(className, true, mixinLoader());
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
        {
            types[i] = args[i].getClass();
        }
        return clazz.getMethod(method, types).invoke(null, args);
    }

    private static void setObfuscationContext(String context) throws Exception
    {
        Class<?> envClass = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment", true, mixinLoader());
        Object env = envClass.getMethod("getDefaultEnvironment").invoke(null);
        env.getClass().getMethod("setObfuscationContext", String.class).invoke(env, context);
    }

    private static void registerErrorHandler()
    {
        try
        {
            Class<?> mixins = Class.forName("org.spongepowered.asm.mixin.Mixins", true, mixinLoader());
            mixins.getMethod("registerErrorHandlerClass", String.class).invoke(null, "cpw.mods.fml.common.asm.mixin.MixinCrashErrorHandler");
            FMLRelaunchLog.fine("Mixin crash error handler registered");
        }
        catch (Exception e)
        {
            FMLRelaunchLog.warning("Could not register Mixin crash error handler: %s", e.toString());
        }
    }

    /**
     * Excludes mixin-unsafe transformers from Mixin's internal class reading.
     *
     * <p>Mixin reads target classes through the registered transformers; a
     * transformer that strips members or emits bytecode Mixin's reader cannot
     * handle breaks that analysis. Exclusions are opt-in via
     * {@code -Dfml.mixin.excludedTransformers=com.foo.,com.bar.Baz} (class
     * name prefixes, comma separated). Transformers implementing
     * {@link IMixinSafeTransformer} (or Makamys' equivalent marker, honoured
     * by name) and everything under {@code cpw.mods.fml.} are always exempt.
     * The idea follows Makamys' Mixingasm (public domain).</p>
     */
    static void refreshTransformerExclusions(LaunchClassLoader classLoader)
    {
        String property = System.getProperty("fml.mixin.excludedTransformers", "").trim();
        if (property.isEmpty())
        {
            return;
        }
        try
        {
            ClassLoader runtimeLoader = mixinLoader();
            Class<?> serviceClass = Class.forName("org.spongepowered.asm.service.MixinService", true, runtimeLoader);
            Object service = serviceClass.getMethod("getService").invoke(null);
            Object provider = service.getClass().getMethod("getTransformerProvider").invoke(service);
            if (provider == null)
            {
                return;
            }
            java.lang.reflect.Method addExclusion = provider.getClass().getMethod("addTransformerExclusion", String.class);
            for (Object transformer : classLoader.getTransformers())
            {
                if (transformer == null)
                {
                    continue;
                }
                String name = transformer.getClass().getName();
                if (isExemptFromExclusion(transformer.getClass(), name))
                {
                    continue;
                }
                for (String pattern : property.split(","))
                {
                    pattern = pattern.trim();
                    if (!pattern.isEmpty() && (name.equals(pattern) || name.startsWith(pattern)))
                    {
                        FMLRelaunchLog.info("Excluding transformer %s from Mixin preprocessing (fml.mixin.excludedTransformers)", name);
                        addExclusion.invoke(provider, name);
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            FMLRelaunchLog.warning("Could not apply Mixin transformer exclusions: %s", e.toString());
        }
    }

    private static boolean isExemptFromExclusion(Class<?> transformerClass, String name)
    {
        if (name.startsWith("cpw.mods.fml."))
        {
            return true;
        }
        try
        {
            for (Class<?> iface : transformerClass.getInterfaces())
            {
                String ifaceName = iface.getName();
                if (ifaceName.equals("cpw.mods.fml.common.asm.mixin.IMixinSafeTransformer")
                        || ifaceName.equals("makamys.mixingasm.api.IMixinSafeTransformer"))
                {
                    return true;
                }
            }
            for (java.lang.annotation.Annotation annotation : transformerClass.getAnnotations())
            {
                if (annotation.annotationType().getName().equals("makamys.mixingasm.api.MixinSafeTransformer"))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            // ignore - absence of proof is not proof of safety
        }
        return false;
    }
}
