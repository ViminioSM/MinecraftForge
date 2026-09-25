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

package cpw.mods.fml.common.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * Rewrites references to foreign shaded ASM packages to the ASM this runtime
 * actually ships ({@code org.objectweb.asm}).
 *
 * <p>Mods built against other Mixin ecosystems may reference shaded copies
 * ({@code org.spongepowered.asm.lib} from Mixin 0.7-era loaders,
 * {@code org.spongepowered.libraries.org.objectweb.asm} from MixinBooter-era
 * loaders) which do not exist here. Left alone, those references break on
 * modern runtimes. The idea follows UniMixins' ASM remapper (Unlicense, by
 * LegacyModdingMC), reimplemented for FML's single-Mixin runtime.</p>
 */
public class ASMRemapperTransformer implements IClassTransformer
{
    private static final String OURS = "org/objectweb/asm/";
    private static final String[] FOREIGN = {
        "org/spongepowered/asm/lib/",
        "org/spongepowered/libraries/org/objectweb/asm/"
    };
    private static final byte[] MARKER = "org/spongepowered/".getBytes();

    private static final Remapper REMAPPER = new Remapper()
    {
        @Override
        public String map(String internalName)
        {
            for (String prefix : FOREIGN)
            {
                if (internalName.startsWith(prefix))
                {
                    return OURS + internalName.substring(prefix.length());
                }
            }
            return super.map(internalName);
        }
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null || !mightContainForeignASM(bytes))
        {
            return bytes;
        }
        try
        {
            ClassReader reader = new ClassReader(bytes);
            // No frame recompute: renaming references never changes stack shapes.
            ClassWriter writer = new ClassWriter(reader, 0);
            reader.accept(new ClassRemapper(writer, REMAPPER), 0);
            FMLRelaunchLog.fine("Remapped shaded ASM references in %s", transformedName);
            return writer.toByteArray();
        }
        catch (Exception e)
        {
            FMLRelaunchLog.warning("Could not remap shaded ASM references in %s: %s", transformedName, e.toString());
            return bytes;
        }
    }

    private static boolean mightContainForeignASM(byte[] bytes)
    {
        outer:
        for (int i = 0; i <= bytes.length - MARKER.length; i++)
        {
            for (int j = 0; j < MARKER.length; j++)
            {
                if (bytes[i + j] != MARKER[j])
                {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }
}
