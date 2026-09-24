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

package cpw.mods.fml.common.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import sun.misc.Unsafe;

/**
 * Writes injected holder values into static fields.
 *
 * <p>Annotated holder fields are made genuinely non-final at class-load time
 * by {@code ObjectHolderTransformer}, so a plain reflective write suffices
 * for them. Fields that are still final at injection time (vanilla classes
 * carry no holder annotations for the transformer to find) are written
 * through {@link Unsafe}, which bypasses the final check. The old approach
 * (rewriting {@code Field.modifiers} via {@code sun.reflect} internals) no
 * longer exists on modern runtimes.</p>
 *
 * <p>This is safe in practice because holder injection runs during preInit,
 * before any game code reading these fields has been JIT-compiled.</p>
 */
final class UnsafeHolderWriter
{
    private static final Unsafe UNSAFE = loadUnsafe();

    private UnsafeHolderWriter()
    {
    }

    private static Unsafe loadUnsafe()
    {
        try
        {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    static void setStatic(Field field, Object value) throws Exception
    {
        field.setAccessible(true);
        if (!Modifier.isFinal(field.getModifiers()) || UNSAFE == null || field.getType().isPrimitive())
        {
            field.set(null, value);
            return;
        }
        Object base = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);
        UNSAFE.putObject(base, offset, value);
    }
}
