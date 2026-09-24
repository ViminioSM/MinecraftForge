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

package cpw.mods.fml.relauncher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;

import org.apache.logging.log4j.Level;

/**
 * Drop-in replacement entry point for {@code net.minecraft.launchwrapper.Launch}
 * that also runs on Java 9+ runtimes.
 *
 * <p>Legacy {@code Launch} obtains the game classpath by casting the system
 * classloader to {@code URLClassLoader}, which throws
 * {@code ClassCastException} on Java 9 and later (the application classloader
 * is no longer a {@code URLClassLoader}). This wrapper builds the identical
 * {@link LaunchClassLoader} setup instead: from the system classloader when it
 * is a {@code URLClassLoader} (Java 8 and earlier), otherwise from the
 * {@code java.class.path} entries. It populates the same {@link Launch}
 * static state ({@code classLoader}, {@code blackboard},
 * {@code minecraftHome}, {@code assetsDir}) and runs the same tweaker
 * discovery/injection sequence, so all existing tweakers and coremods work
 * unchanged.</p>
 */
public class FMLLaunchWrapper
{
    public static void main(String[] args)
    {
        new FMLLaunchWrapper().launch(args);
    }

    private void launch(String[] args)
    {
        LaunchClassLoader classLoader = new LaunchClassLoader(getGameClassPath());
        Launch.classLoader = classLoader;
        Launch.blackboard = new HashMap<String, Object>();
        Thread.currentThread().setContextClassLoader(classLoader);

        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        final OptionSpec<String> profileOption = parser.accepts("version", "The version we launched with").withRequiredArg();
        final OptionSpec<File> gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
        final OptionSpec<File> assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
        final OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg();
        final OptionSpec<String> nonOption = parser.nonOptions();

        final OptionSet options = parser.parse(args);
        Launch.minecraftHome = options.valueOf(gameDirOption);
        Launch.assetsDir = options.valueOf(assetsDirOption);
        final String profileName = options.valueOf(profileOption);
        final List<String> tweakClassNames = new ArrayList<String>(options.valuesOf(tweakClassOption));

        final List<String> argumentList = new ArrayList<String>();
        Launch.blackboard.put("TweakClasses", tweakClassNames);
        Launch.blackboard.put("ArgumentList", argumentList);

        final Set<String> allTweakerNames = new HashSet<String>();
        final List<ITweaker> allTweakers = new ArrayList<ITweaker>();
        try
        {
            final List<ITweaker> tweakers = new ArrayList<ITweaker>(tweakClassNames.size() + 1);
            Launch.blackboard.put("Tweaks", tweakers);
            ITweaker primaryTweaker = null;
            do
            {
                for (final Iterator<String> it = tweakClassNames.iterator(); it.hasNext(); )
                {
                    final String tweakName = it.next();
                    if (allTweakerNames.contains(tweakName))
                    {
                        LogWrapper.log(Level.WARN, "Tweak class name %s has already been visited -- skipping", tweakName);
                        it.remove();
                        continue;
                    }
                    else
                    {
                        allTweakerNames.add(tweakName);
                    }
                    LogWrapper.log(Level.INFO, "Loading tweak class name %s", tweakName);

                    classLoader.addClassLoaderExclusion(tweakName.substring(0, tweakName.lastIndexOf('.')));
                    final ITweaker tweaker = (ITweaker) Class.forName(tweakName, true, classLoader).getDeclaredConstructor().newInstance();
                    tweakers.add(tweaker);

                    it.remove();
                    if (primaryTweaker == null)
                    {
                        LogWrapper.log(Level.INFO, "Using primary tweak class name %s", tweakName);
                        primaryTweaker = tweaker;
                    }
                }

                for (final Iterator<ITweaker> it = tweakers.iterator(); it.hasNext(); )
                {
                    final ITweaker tweaker = it.next();
                    LogWrapper.log(Level.INFO, "Calling tweak class %s", tweaker.getClass().getName());
                    tweaker.acceptOptions(options.valuesOf(nonOption), Launch.minecraftHome, Launch.assetsDir, profileName);
                    tweaker.injectIntoClassLoader(classLoader);
                    allTweakers.add(tweaker);
                    it.remove();
                }
            } while (!tweakClassNames.isEmpty());

            for (final ITweaker tweaker : allTweakers)
            {
                argumentList.addAll(Arrays.asList(tweaker.getLaunchArguments()));
            }

            final String launchTarget = primaryTweaker.getLaunchTarget();
            final Class<?> clazz = Class.forName(launchTarget, false, classLoader);
            final Method mainMethod = clazz.getMethod("main", new Class[] { String[].class });

            LogWrapper.info("Launching wrapped minecraft {%s}", launchTarget);
            mainMethod.invoke(null, (Object) argumentList.toArray(new String[argumentList.size()]));
        }
        catch (Exception e)
        {
            LogWrapper.log(Level.ERROR, e, "Unable to launch");
            System.exit(1);
        }
    }

    /**
     * Resolves the game classpath URLs. On Java 8 and earlier the system
     * classloader is a {@code URLClassLoader} and its URLs are used directly;
     * on Java 9+ the {@code java.class.path} entries are converted instead.
     */
    private static URL[] getGameClassPath()
    {
        ClassLoader systemLoader = FMLLaunchWrapper.class.getClassLoader();
        if (systemLoader instanceof URLClassLoader)
        {
            return ((URLClassLoader) systemLoader).getURLs();
        }

        String[] entries = System.getProperty("java.class.path", "").split(java.util.regex.Pattern.quote(File.pathSeparator));
        List<URL> urls = new ArrayList<URL>(entries.length);
        for (String entry : entries)
        {
            if (entry == null || entry.isEmpty())
            {
                continue;
            }
            try
            {
                urls.add(new File(entry).toURI().toURL());
            }
            catch (Exception e)
            {
                System.err.println("FMLLaunchWrapper: ignoring bad classpath entry: " + entry);
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }
}
