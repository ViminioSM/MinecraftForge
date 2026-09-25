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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * Queues Mixin configurations from coremods (early) and regular mods (late).
 *
 * <p>Early configs come from coremod plugins implementing
 * {@link IFMLEarlyMixinLoader} and are queued during coremod injection, before
 * game classes load. Late configs come from the {@code "mixins"} array in
 * {@code mcmod.info} and are queued just before mod construction, followed by
 * a refresh of Mixin's selected/prepared configs so they actually apply.</p>
 *
 * <p>The late-refresh sequence (re-running {@code selectConfigs} and
 * {@code prepareConfigs} on Mixin's processor) follows the orchestration
 * idiom established by MixinBooterLegacy's {@code LoadControllerMixin}
 * (LGPLv2.1, by tox1cozz), reimplemented here against FML's own hooks
 * instead of a Mixin-on-FML self-host.</p>
 */
public class FMLMixinController
{
    private FMLMixinController()
    {
    }

    public static void queueEarlyConfigs(IFMLLoadingPlugin plugin, File location, LaunchClassLoader classLoader)
    {
        if (!(plugin instanceof IFMLEarlyMixinLoader))
        {
            return;
        }
        if (!FMLMixinBootstrap.isInitialised())
        {
            FMLRelaunchLog.warning("Coremod %s offers early Mixin configs but Mixin support is not initialised - ignoring", plugin.getClass().getName());
            return;
        }
        List<String> configs = ((IFMLEarlyMixinLoader) plugin).getMixinConfigs();
        if (configs == null)
        {
            return;
        }
        ensureOnClasspath(location, classLoader);
        for (String config : configs)
        {
            FMLRelaunchLog.info("Queuing early Mixin config %s from %s", config, plugin.getClass().getName());
            addConfiguration(config, classLoader);
        }
    }

    /**
     * Called from {@code Loader.loadMods} after mod discovery, before the
     * CONSTRUCTING state is distributed.
     */
    public static void onLateConfigs(ModClassLoader modClassLoader, List<ModContainer> activeMods)
    {
        if (!FMLMixinBootstrap.isInitialised())
        {
            return;
        }
        LaunchClassLoader classLoader = Launch.classLoader;
        boolean queued = false;
        FMLMixinBootstrap.refreshTransformerExclusions(classLoader);
        for (ModContainer container : activeMods)
        {
            String[] configs = container.getMetadata() != null ? container.getMetadata().mixins : null;
            if (configs == null || configs.length == 0)
            {
                continue;
            }
            File source = container.getSource();
            if (source != null && source.isFile())
            {
                ensureOnClasspath(source, classLoader);
                try
                {
                    modClassLoader.addFile(source);
                }
                catch (Exception e)
                {
                    FMLRelaunchLog.warning("Could not add %s to the mod classloader for Mixin: %s", source, e.toString());
                }
            }
            for (String config : configs)
            {
                FMLRelaunchLog.info("Queuing late Mixin config %s from mod %s", config, container.getModId());
                addConfiguration(config, classLoader);
                queued = true;
            }
        }
        if (queued)
        {
            refreshConfigs(classLoader);
        }
    }

    private static void ensureOnClasspath(File file, LaunchClassLoader classLoader)
    {
        try
        {
            URL url = file.toURI().toURL();
            for (URL existing : classLoader.getSources())
            {
                if (existing.equals(url))
                {
                    return;
                }
            }
            classLoader.addURL(url);
        }
        catch (Exception e)
        {
            FMLRelaunchLog.warning("Could not add %s to the classloader for Mixin: %s", file, e.toString());
        }
    }

    private static void addConfiguration(String config, LaunchClassLoader classLoader)
    {
        try
        {
            // Resolved through the Mixin runtime loader so every caller,
            // whichever loader defined it, queues into the single runtime.
            // NB: the two-arg overload is required on Mixin 0.8.x - the
            // single-arg form passes a null fallback environment, which makes
            // config creation fail for configs without an explicit selector.
            // The current phase environment is used so the config is visited
            // by the selectConfigs refresh below instead of waiting for a
            // later phase.
            ClassLoader runtimeLoader = FMLMixinBootstrap.mixinLoader();
            Class<?> mixins = Class.forName("org.spongepowered.asm.mixin.Mixins", true, runtimeLoader);
            Class<?> envClass = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment", true, runtimeLoader);
            Object currentEnv = envClass.getMethod("getCurrentEnvironment").invoke(null);
            try
            {
                // Preferred: queue against the current phase environment so the
                // config is visited by the refresh below. (Package-private on
                // 0.8.x, hence reflective.)
                java.lang.reflect.Method add = mixins.getDeclaredMethod("addConfiguration", String.class, envClass);
                add.setAccessible(true);
                add.invoke(null, config, currentEnv);
            }
            catch (NoSuchMethodException e)
            {
                Class<?> sourceClass = Class.forName("org.spongepowered.asm.mixin.extensibility.IMixinConfigSource", true, runtimeLoader);
                mixins.getMethod("addConfiguration", String.class, sourceClass).invoke(null, config, new Object[] { null });
            }
        }
        catch (Exception e)
        {
            FMLRelaunchLog.log(org.apache.logging.log4j.Level.WARN, e, "Could not queue Mixin config %s", config);
        }
    }

    /**
     * Mixin freezes its config selection when the first class transforms;
     * configs queued afterwards (the late ones) need an explicit refresh.
     * Adapted from MixinBooterLegacy's LoadControllerMixin orchestration.
     */
    private static void refreshConfigs(LaunchClassLoader classLoader)
    {
        try
        {
            Object proxy = null;
            for (Object transformer : classLoader.getTransformers())
            {
                if (transformer != null && FMLMixinBootstrap.MIXIN_PROXY_CLASS.equals(transformer.getClass().getName()))
                {
                    proxy = transformer;
                    break;
                }
            }
            if (proxy == null)
            {
                FMLRelaunchLog.warning("Late Mixin configs queued but the Mixin transformer is not registered - they will not apply");
                return;
            }
            ClassLoader runtimeLoader = FMLMixinBootstrap.mixinLoader();
            Field transformerField = proxy.getClass().getDeclaredField("transformer");
            transformerField.setAccessible(true);
            Object transformer = transformerField.get(proxy);

            Class<?> mixinTransformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer", true, runtimeLoader);
            Field processorField = mixinTransformerClass.getDeclaredField("processor");
            processorField.setAccessible(true);
            Object processor = processorField.get(transformer);

            Class<?> mixinProcessorClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinProcessor", true, runtimeLoader);
            Class<?> envClass = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment", true, runtimeLoader);
            Object env = envClass.getMethod("getCurrentEnvironment").invoke(null);

            Method selectConfigs = mixinProcessorClass.getDeclaredMethod("selectConfigs", envClass);
            selectConfigs.setAccessible(true);
            selectConfigs.invoke(processor, env);

            try
            {
                Method prepareConfigs = mixinProcessorClass.getDeclaredMethod("prepareConfigs", envClass);
                prepareConfigs.setAccessible(true);
                prepareConfigs.invoke(processor, env);
            }
            catch (NoSuchMethodException e)
            {
                // Mixin 0.8.3+: prepareConfigs takes the extensions registry.
                Class<?> extensionsClass = Class.forName("org.spongepowered.asm.mixin.transformer.ext.Extensions", true, runtimeLoader);
                Method prepareConfigs = mixinProcessorClass.getDeclaredMethod("prepareConfigs", envClass, extensionsClass);
                prepareConfigs.setAccessible(true);
                Field extensionsField = mixinProcessorClass.getDeclaredField("extensions");
                extensionsField.setAccessible(true);
                prepareConfigs.invoke(processor, env, extensionsField.get(processor));
            }
            FMLRelaunchLog.info("Mixin configs refreshed for late mixins");
        }
        catch (Exception e)
        {
            FMLRelaunchLog.warning("Could not refresh Mixin configs for late mixins: %s", e.toString());
        }
    }
}
