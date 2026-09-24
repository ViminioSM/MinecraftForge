*** HOW TO INSTALL ***
For Mod Users:

Download the latest installer from https://files.minecraftforge.net and follow instructions given by the installer.


For Mod Devs:

Download the latest Forge source distribution from https://files.minecraftforge.net and unzip it to a folder.
Open a command prompt, navigate to the directory where you unzipped the Forge sources, and run:
If you have Gradle: gradle setupDevWorkspace
If you DO NOT have Gradle installed:
Windows: ./gradlew.bat setupDevWorkspace
MacOS/Linux: ./gradlew setupDevWorkspace

If you wish to use the Eclipse IDE, run gradle eclipse instead of gradle setupDevWorkspace, or install the Gradle plugin for Eclipse and import the Forge source folder as a Gradle project.

To get the decompiled classes:
If you have Gradle: gradle setupDecompWorkspace
If you DO NOT have Gradle installed:
Windows: ./gradlew.bat setupDecompWorkspace
MacOS/Linux: ./gradlew setupDecompWorkspace

For Contributors: (Note: This assumes you have Gradle installed. If you don't, use ./gradlew(.bat) instead of gradle.

Clone this repository to a folder. 
Open a command prompt and navigate to the folder where you cloned this repo.
Run gradle setupForge to setup your development environment.

To use Eclipse, point your Eclipse workspace at the eclipse folder inside the repo.


Requirements (for both mod devs and contributors):
  You must have a JDK installed and accessible.
  If you do not wish to use the gradle wrapper, you can install Gradle from https://gradle.org/ .


*** RUNNING ON MODERN JAVA (JDK 21) ***
This branch builds and runs on JDK 21 (server verified, client verified up to
the main-menu pipeline with GPU rendering).

Building still requires a JDK 8 to drive the legacy toolchain
(ForgeGradle 1.2 + Gradle 2.x cannot start on modern runtimes): set JAVA_HOME
to a JDK 8 before running gradlew. The produced jars target bytecode 52
(Java 8) and run on JDK 21.

Running on JDK 21: do NOT use net.minecraft.launchwrapper.Launch as the main
class (it casts the system classloader to URLClassLoader, which no longer
exists). Use cpw.mods.fml.relauncher.FMLLaunchWrapper instead - it sets up the
identical LaunchClassLoader/tweaker pipeline from java.class.path. Example
(server): java -cp <forge-universal>:<libraries>:<minecraft_server> \
  cpw.mods.fml.relauncher.FMLLaunchWrapper \
  --tweakClass cpw.mods.fml.common.launcher.FMLServerTweaker nogui
Important: put the modern library jars BEFORE minecraft_server.jar on the
classpath. Mojang's 1.7.10 server jar bundles stale copies of guava, gson,
trove4j, netty, authlib, commons and log4j classes which would otherwise
shadow the updated dependencies. Optionally pass
-Djava.security.manager=allow to re-enable FML's exit-trapping security
manager (without it, exit-trapping gracefully disables itself).


*** CREDITS FOR THE JDK 21 WORK ***
No third-party code was vendored: every fix in this branch is an original
implementation. The following external projects, artifacts and services were
used or guided the design:

Design guidance (techniques reimplemented from scratch, no code copied):
  GTNewHorizons lwjgl3ify by eigenraven and contributors
  (https://github.com/GTNewHorizons/lwjgl3ify) - the proven recipe for
  1.7.10 on modern Java: upgrading ASM toward 9.x, replacing sun.reflect
  field/enum hacks, and running with -Djava.security.manager=allow.
  Toolchain landscape evaluated but not adopted: GTNewHorizons
  RetroFuturaGradle, anatawa12's ForgeGradle 1.2 fork, MCPHackers LaunchWrapper.

Updated third-party dependencies (Maven Central, see fml/jsons/):
  OW2 ASM 9.6 (asm, asm-tree, asm-commons) - replaces asm-all/asm-debug-all 5.x
  Guava 21.0, Scala 2.11.12, Akka 2.3.16, Typesafe Config 1.4.3,
  Commons-Lang3 3.14.0, jopt-simple 4.9.

Build/runtime artifacts (Mojang, for the offline workspace cache):
  piston-meta.mojang.com, launchermeta.mojang.com, launcher.mojang.com,
  resources.download.minecraft.net, libraries.minecraft.net.

Build bridge: Eclipse Temurin JDK 8 (https://adoptium.net), needed only to
run the legacy Gradle toolchain; the game itself runs on JDK 21.
 
