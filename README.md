# MinecraftForge

Forge is a free, open-source modding API all of your favourite mods use!

This branch targets **Minecraft 1.7.10** and additionally runs on **JDK 21**
(see [Running on JDK 21](#running-on-jdk-21)).

- [Download](https://files.minecraftforge.net/)
- [Forum](https://forums.minecraftforge.net/)
- [Discord](https://discord.minecraftforge.net/)
- [Documentation](https://docs.minecraftforge.net/)

# Installing Forge

Go to [the Forge website](https://files.minecraftforge.net/) and select
`1.7.10` from the Minecraft version list.

Download the installer for the *Recommended Build* or the *Latest build*
there. The installer will attempt to install Forge into your vanilla launcher
environment, where you can then create a new profile using that version and
play the game!

For support and questions, visit [the Support Forum](https://forums.minecraftforge.net/forum/18-support-bug-reports/)
or [the Forge Discord server](https://discord.minecraftforge.net/).

## Required Java Versions

Minecraft 1.7.10 officially requires Java 8. This branch additionally
supports modern runtimes:

| Minecraft | Java (classic) | Java (this branch) |
|-----------|----------------|--------------------|
| 1.7.10    | [8](https://adoptium.net/temurin/releases/?version=8) | 8 and [21](https://adoptium.net/temurin/releases/?version=21) |

**If you are on Windows and have difficulty using Java to launch JAR files,
consider using [Jarfix by Johann Löfflmann](https://johann.loefflmann.net/en/software/jarfix/index.html).**

# Running on JDK 21

The dedicated server is verified booting to `Done` on JDK 21, including a
mod that exercises `EnumHelper` and `@ObjectHolder` injection; the client
boots through the full FML pipeline (splash, sounds, textures, OpenGL).

Building still requires a JDK 8 to drive the legacy toolchain
(ForgeGradle 1.2 + Gradle 2.x cannot start on modern runtimes): point
`JAVA_HOME` at a JDK 8 before running `gradlew`. The produced jars target
bytecode 52 (Java 8) and run on JDK 21. Note the legacy Mojang download
endpoints used by the setup pipeline are dead, so the workspace setup
expects the Minecraft artifacts (assets index/objects, client/server jars,
version json) to be pre-seeded in the Gradle cache.

Running on JDK 21: do NOT use `net.minecraft.launchwrapper.Launch` as the
main class (it casts the system classloader to `URLClassLoader`, which no
longer exists). Use `cpw.mods.fml.relauncher.FMLLaunchWrapper` instead - it
sets up the identical `LaunchClassLoader`/tweaker pipeline from
`java.class.path`. Example (server):

```
java -cp <forge-universal>:<libraries>:<minecraft_server> \
  cpw.mods.fml.relauncher.FMLLaunchWrapper \
  --tweakClass cpw.mods.fml.common.launcher.FMLServerTweaker nogui
```

Important: put the modern library jars BEFORE `minecraft_server.jar` on the
classpath. Mojang's 1.7.10 server jar bundles stale copies of guava, gson,
trove4j, netty, authlib, commons and log4j classes which would otherwise
shadow the updated dependencies. Optionally pass
`-Djava.security.manager=allow` to re-enable FML's exit-trapping security
manager (without it, exit-trapping gracefully disables itself).

# Creating Mods

For Mod Devs: download the latest Forge source distribution and unzip it to
a folder, then run `gradlew setupDevWorkspace` (or `setupDecompWorkspace`
for decompiled classes). To use Eclipse, run `gradle eclipse` instead, or
import the sources as a Gradle project.

For Contributors: clone this repository, run `gradle setupForge` to set up
the development environment, and point your Eclipse workspace at the
`eclipse` folder inside the repo.

[See the "Getting Started" section in the Forge Documentation](https://docs.minecraftforge.net/en/latest/gettingstarted/).

# Contribute to Forge

If you wish to actually inspect Forge, submit PRs or otherwise work with
Forge itself, you're in the right place!

### Pull requests

Please read the contributing guidelines found in `CONTRIBUTING.md` before
making a pull request (GSD-style workflow: discuss, plan, execute, verify).

### Contributor License Agreement

We require all contributors to acknowledge the
[Forge Contributor License Agreement](https://cla-assistant.io/MinecraftForge/MinecraftForge).
Please ensure you have a valid email address associated with your GitHub
account to do this.

#### Donate

*Forge is a large project with many collaborators working on it around the
clock. Forge is and will always remain free to use and modify. However, it
costs money to run such a large project as this, so please consider
[becoming a patron](https://www.patreon.com/LexManos).*

# Credits

The classic credits live in `MinecraftForge-Credits.txt` (Forge),
`fml/CREDITS-fml.txt` (FML) and the `Paulscode * License.txt` files.

No third-party code was vendored by the JDK 21 work: every fix in this
branch is an original implementation. The following external projects,
artifacts and services were used or guided the design:

Design guidance (techniques reimplemented from scratch, no code copied):
- GTNewHorizons **lwjgl3ify** by eigenraven and contributors
  (https://github.com/GTNewHorizons/lwjgl3ify) - the proven recipe for
  1.7.10 on modern Java: upgrading ASM toward 9.x, replacing `sun.reflect`
  field/enum hacks, and running with `-Djava.security.manager=allow`.
- Toolchain landscape evaluated but not adopted: GTNewHorizons
  **RetroFuturaGradle**, anatawa12's **ForgeGradle 1.2 fork**, MCPHackers
  **LaunchWrapper**.

Updated third-party dependencies (Maven Central, see `fml/jsons/`):
- OW2 ASM 9.6 (`asm`, `asm-tree`, `asm-commons`) - replaces
  `asm-all`/`asm-debug-all` 5.x
- Guava 21.0, Scala 2.11.12, Akka 2.3.16, Typesafe Config 1.4.3,
  Commons-Lang3 3.14.0, jopt-simple 4.9.

Build/runtime artifacts (Mojang, for the offline workspace cache):
- `piston-meta.mojang.com`, `launchermeta.mojang.com`,
  `launcher.mojang.com`, `resources.download.minecraft.net`,
  `libraries.minecraft.net`.

Build bridge: **Eclipse Temurin JDK 8** (https://adoptium.net), needed only
to run the legacy Gradle toolchain; the game itself runs on JDK 21.

Native Mixin support (SpongePowered **Mixin 0.8.7**, MIT -
https://github.com/SpongePowered/Mixin) is built into FML: mods declare
`"mixins": [...]` in `mcmod.info`, coremods may implement
`IFMLEarlyMixinLoader`. The late-config orchestration (refreshing Mixin's
selected/prepared configs around mod construction) adapts the idiom from
LegacyModdingMC **UniMixins** `mixinbooterlegacy` (LGPLv2.1, by tox1cozz -
https://github.com/LegacyModdingMC/UniMixins) with attribution; no
LGPLv3-licensed loader code (GTNH `gtnhmixins`, `spongemixins`) was taken.
