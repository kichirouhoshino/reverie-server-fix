# reverie-server-fix

**reverie-server-fix** is a lightweight mod designed to fix server-side issues in the *Casket of Reveries* modpack.

## Fixes

- **Epic Fight Nightfall crash fix:** Fixed crashes caused by Epic Fight Nightfall loading client-side visual effects on the server.
- **Cataclysm Dimension Teleport Fix:** Automatically detects and forcefully clears the "Rebuilding" flag in the `cataclysm_dimension` mod if a player successfully teleports (TPA) into the dimension but gets stuck in a falsely triggered rebuilding state.

## Creating the Casket of Reveries server

Tested on version 2.2.7 of the modpack. This is a **server-side only** mod! You do not need to install this on the client

1. Download the jar file in the releases section.
2. Download the forge installer, specifically version 47.4.4 for 1.20.1. Install the server in a folder.
3. Install the modpack on curseforge and launch it once. Copy both the `mods` and `config` folders to your dedicated server folder.
4. Drop the reverie-server-fix.jar into your dedicated server's `mods` folder.


## Building

To build the mod from source:
```bash
./gradlew build
```
The final jar will be located in `build/libs/`.

*Note: Building requires the EpicFight mod jar in the `libs` folder as a `compileOnly` dependency due to the direct mixin against `LivingEntityPatch`.*

## Note

This mod was created with the help of Generative AI. I have heavily tested this on our private playthroughs, but if you're against it, please don't use this mod. I made this personally to be able to host a server for me and my friends. Please wait for the release of the modpack's server files if you want something more official.