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

/**
 * Implemented by class transformers which are safe for Mixin's internal
 * class reading: they neither remove members Mixin may need nor produce
 * bytecode Mixin's ASM reader cannot handle.
 *
 * <p>Transformers carrying this marker (or Makamys' equivalent
 * {@code makamys.mixingasm.api.IMixinSafeTransformer}, honoured by name for
 * cross-mod compatibility) are exempt from {@code fml.mixin.excludedTransformers}
 * exclusions. FML's own transformers ({@code cpw.mods.fml.*}) are always
 * exempt. The concept follows Makamys' Mixingasm (public domain).</p>
 */
public interface IMixinSafeTransformer
{
}
