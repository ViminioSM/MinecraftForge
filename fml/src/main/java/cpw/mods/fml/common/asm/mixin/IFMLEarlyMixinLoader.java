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

import java.util.List;

/**
 * Implemented by coremod plugins ({@code IFMLLoadingPlugin}) that ship Mixin
 * configurations which must apply before game classes load (early mixins).
 *
 * <p>Queued configurations are registered with Mixin when the coremod
 * injects; see {@link FMLMixinController}.</p>
 */
public interface IFMLEarlyMixinLoader
{
    /**
     * @return Mixin config resource names (e.g. {@code "mixins.modid.early.json"}),
     *         resolved from the coremod jar.
     */
    List<String> getMixinConfigs();
}
