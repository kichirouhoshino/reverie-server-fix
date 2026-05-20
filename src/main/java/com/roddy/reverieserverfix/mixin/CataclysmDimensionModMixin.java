package com.roddy.reverieserverfix.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Map;

@Mixin(targets = "com.p1nero.cataclysm_dimension.CataclysmDimensionMod", remap = false)
public class CataclysmDimensionModMixin {

    @Inject(method = "onServerLevelTick", at = @At("HEAD"))
    private void distguard_beforeServerLevelTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }

        try {
            // Get the static fields of CataclysmDimensionMod reflectively to avoid hard dependencies at compile time
            Class<?> modClass = Class.forName("com.p1nero.cataclysm_dimension.CataclysmDimensionMod");
            
            Field integerMapField = modClass.getDeclaredField("RESOURCE_LOCATION_INTEGER_MAP");
            integerMapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<ResourceLocation, Integer> resourceLocationIntegerMap = (Map<ResourceLocation, Integer>) integerMapField.get(null);

            Class<?> dimensionsClass = Class.forName("com.p1nero.cataclysm_dimension.worldgen.CataclysmDimensions");
            Field levelsField = dimensionsClass.getDeclaredField("LEVELS");
            levelsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<ResourceKey<Level>> levels = (java.util.List<ResourceKey<Level>>) levelsField.get(null);

            if (resourceLocationIntegerMap != null && levels != null) {
                for (ResourceKey<Level> levelResourceKey : levels) {
                    ResourceLocation resourceLocation = levelResourceKey.location();
                    int current = resourceLocationIntegerMap.getOrDefault(resourceLocation, 0);
                    // The mod executes the reset/file deletion when current == 1
                    if (current == 1) {
                        ServerLevel serverLevel = server.getLevel(levelResourceKey);
                        if (serverLevel != null) {
                            distguard_safelyCloseRegionFiles(serverLevel);
                        }
                    }
                }
            }
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error("[ReverieServerFix] Error pre-handling Cataclysm dimension reset: ", e);
        }
    }

    private static void distguard_safelyCloseRegionFiles(ServerLevel serverLevel) {
        try {
            // Dynamically search for the worker field inside ChunkStorage class (which ChunkMap extends)
            Field workerField = null;
            for (Field field : net.minecraft.world.level.chunk.storage.ChunkStorage.class.getDeclaredFields()) {
                if (field.getType() == IOWorker.class) {
                    workerField = field;
                    break;
                }
            }

            if (workerField == null) {
                com.mojang.logging.LogUtils.getLogger().warn("[ReverieServerFix] Could not find worker field in ChunkStorage.");
                return;
            }

            workerField.setAccessible(true);
            IOWorker ioWorker = (IOWorker) workerField.get(serverLevel.getChunkSource().chunkMap);
            if (ioWorker == null) {
                return;
            }

            // Find storage field inside IOWorker
            Field storageField = null;
            for (Field field : IOWorker.class.getDeclaredFields()) {
                if (field.getType() == RegionFileStorage.class) {
                    storageField = field;
                    break;
                }
            }

            if (storageField == null) {
                com.mojang.logging.LogUtils.getLogger().warn("[ReverieServerFix] Could not find storage field in IOWorker.");
                return;
            }

            storageField.setAccessible(true);
            RegionFileStorage regionFileStorage = (RegionFileStorage) storageField.get(ioWorker);
            if (regionFileStorage == null) {
                return;
            }

            // Find regionCache field inside RegionFileStorage
            Field regionCacheField = null;
            for (Field field : RegionFileStorage.class.getDeclaredFields()) {
                if (field.getName().equals("regionCache") || 
                    field.getName().equals("f_63701_") || 
                    field.getType().getName().contains("Long2ObjectLinkedOpenHashMap") || 
                    field.getType().getSimpleName().equals("Long2ObjectLinkedOpenHashMap")) {
                    regionCacheField = field;
                    break;
                }
            }

            if (regionCacheField == null) {
                com.mojang.logging.LogUtils.getLogger().warn("[ReverieServerFix] Could not find regionCache field in RegionFileStorage.");
                return;
            }

            regionCacheField.setAccessible(true);
            it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap<?> regionCache = 
                (it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap<?>) regionCacheField.get(regionFileStorage);

            if (regionCache != null) {
                com.mojang.logging.LogUtils.getLogger().info("[ReverieServerFix] Pre-closing {} open region files for dimension {}", regionCache.size(), serverLevel.dimension().location());
                synchronized (regionCache) {
                    for (Object regionFile : regionCache.values()) {
                        if (regionFile instanceof AutoCloseable) {
                            try {
                                ((AutoCloseable) regionFile).close();
                            } catch (Exception e) {
                                // Ignore
                            }
                        }
                    }
                    regionCache.clear();
                }
                com.mojang.logging.LogUtils.getLogger().info("[ReverieServerFix] Successfully closed open region files and cleared cache.");
            }
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error("[ReverieServerFix] Failed to safely close region files: ", e);
        }
    }
}
