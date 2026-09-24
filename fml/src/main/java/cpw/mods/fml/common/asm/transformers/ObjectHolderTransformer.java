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

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * Strips the {@code final} modifier from {@code static} fields annotated with
 * {@code @ObjectHolder} or {@code @ItemStackHolder} as classes are loaded.
 *
 * <p>On Java 8, FML injected holder values into {@code static final} fields by
 * rewriting the field modifiers through {@code sun.reflect} internals. Those
 * internals ({@code Field.modifiers}, {@code ReflectionFactory} accessors)
 * were removed from the JDK, so on modern runtimes the injection is done with
 * a plain {@link java.lang.reflect.Field#set} instead - which requires the
 * field to be non-final. Removing {@code final} at class-load time (rather
 * than writing through {@code Unsafe}) keeps the field genuinely mutable, so
 * there is no risk of the JIT constant-folding a stale value.</p>
 */
public class ObjectHolderTransformer implements IClassTransformer
{
    private static final String OBJECT_HOLDER_DESC = "Lcpw/mods/fml/common/registry/GameRegistry$ObjectHolder;";
    private static final String ITEMSTACK_HOLDER_DESC = "Lcpw/mods/fml/common/registry/GameRegistry$ItemStackHolder;";
    private static final byte[] OBJECT_HOLDER_MARKER = "ObjectHolder".getBytes();

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null || !mightContainHolder(bytes))
        {
            return bytes;
        }

        ClassReader reader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        // NB: no SKIP_CODE here - the rewritten class must keep its method
        // bodies. The mightContainHolder pre-filter above already limits full
        // parses to classes referencing the holder annotations.
        reader.accept(classNode, 0);

        boolean edited = false;
        for (FieldNode field : (List<FieldNode>) classNode.fields)
        {
            if ((field.access & (ACC_STATIC | ACC_FINAL)) != (ACC_STATIC | ACC_FINAL))
            {
                continue;
            }
            if (hasHolderAnnotation(field.visibleAnnotations) || hasHolderAnnotation(field.invisibleAnnotations))
            {
                field.access &= ~ACC_FINAL;
                edited = true;
            }
        }

        if (!edited)
        {
            return bytes;
        }

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    /**
     * Cheap pre-filter: most classes never reference the holder annotations,
     * so avoid building the ASM tree for them entirely.
     */
    private static boolean mightContainHolder(byte[] bytes)
    {
        outer:
        for (int i = 0; i <= bytes.length - OBJECT_HOLDER_MARKER.length; i++)
        {
            for (int j = 0; j < OBJECT_HOLDER_MARKER.length; j++)
            {
                if (bytes[i + j] != OBJECT_HOLDER_MARKER[j])
                {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean hasHolderAnnotation(List<AnnotationNode> annotations)
    {
        if (annotations == null)
        {
            return false;
        }
        for (AnnotationNode annotation : annotations)
        {
            if (OBJECT_HOLDER_DESC.equals(annotation.desc) || ITEMSTACK_HOLDER_DESC.equals(annotation.desc))
            {
                return true;
            }
        }
        return false;
    }
}
