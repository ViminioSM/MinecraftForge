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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Collects Mixin prepare/apply errors per target class so the crash-report
 * hook ({@code MixinCrashReportTransformer}) can attribute them. Registered
 * with Mixin by {@link FMLMixinBootstrap} when Mixin support initialises.
 *
 * <p>Modelled on UniMixins' error handler (Unlicense, by LegacyModdingMC),
 * reimplemented for FML's own hook point. Returning {@code null} preserves
 * Mixin's default error behaviour - this handler only observes.</p>
 */
public class MixinCrashErrorHandler implements IMixinErrorHandler
{
    private static final Map<String, List<String>> caughtErrors = new HashMap<String, List<String>>();

    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action)
    {
        for (String target : mixin.getTargetClasses())
        {
            putError(target, th);
        }
        return null;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action)
    {
        putError(targetClassName, th);
        return null;
    }

    private static void putError(String className, Throwable th)
    {
        List<String> errors = caughtErrors.get(className);
        if (errors == null)
        {
            errors = new ArrayList<String>();
            caughtErrors.put(className, errors);
        }
        errors.add(String.valueOf(th));
    }

    public static List<String> getErrorsForClass(String className)
    {
        List<String> errors = caughtErrors.get(className);
        return errors != null ? errors : Collections.<String>emptyList();
    }
}
