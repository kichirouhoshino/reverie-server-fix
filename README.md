# reverie-server-fix

**reverie-server-fix** is a lightweight mod designed to fix server-side issues in the *Casket of Reveries* modpack. Note that this is a temporary and band-aid solution until the official server files are released.

## Fixes
- **Epic Fight Nightfall crash fix:** Fixed crashes caused by Epic Fight Nightfall loading client-side visual effects on the server.
- **Cataclysm Dimension Teleport Fix:** Fix players unable to TPA into a cataclysm dimension when there are still players in it. The game showed a misleading "Rebuilding..." message when it actually isn't rebuilding it.
- **Book Of Dragons NPE Fix:** Prevents a massive `NullPointerException` stacktrace spam in the server logs caused by dragons attempting to calculate asynchronous paths to null targets.

## Creating the Casket of Reveries server

Tested on version 2.2.7 of the modpack. This is a **server-side only** mod! You do not need to install this on the client.

1. Download the jar file in the releases section.
2. Download the forge installer, specifically version 47.4.4 for 1.20.1. Install the server in a folder.
3. Install the modpack on CurseForge and launch it once. Copy both the `mods` and `config` folders to your dedicated server folder.
4. **Remove the following client-only mods** from your server's `mods` folder (they will crash the server):

| Mod | Filename (contains) |
|-----|---------------------|
| Oculus (Shaders) | `oculus-` |
| ExtraSounds | `extrasounds` |
| ClearWater | `clearwater` |
| ChatPlus | `chatplus` |
| Just Enough Characters | `jecharacters` |
| Legendary Tooltips | `LegendaryTooltips` |
| Better Lock On | `betterlockon` |

5. Drop the `reverie-server-fix.jar` into your dedicated server's `mods` folder.


## Building from Source

Because this mod injects into existing modpack jar files to fix specific internal bugs, it requires those mods to be present in the compilation classpath. To avoid uploading copyrighted mod jars to GitHub, the `libs/` folder is intentionally ignored in version control.

To compile this mod yourself:
1. Create a folder named `libs` in the root directory of this repository.
2. Copy the following mod jars from your *Casket of Reveries* modpack into the `libs` folder:
    - `epicfight-[version].jar`
    - `bookofdragons-[version].jar`
    - `tcrcore-[version].jar`
3. Run the following command:
```bash
./gradlew build
```
4. The final compiled jar will be located in `build/libs/`.


## Note

This mod was created with the help of Generative AI. I have heavily tested this on our private playthroughs, but if you're against it, please don't use this mod. I made this personally to be able to host a server for me and my friends. Please wait for the release of the modpack's server files if you want something more official.