package com.roddy.reverieserverfix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Map;

@Mixin(targets = "com.p1nero.tcrcore.events.PlayerEventListeners", remap = false)
public class PlayerEventListenersMixin {

    @Inject(method = "onPlayerTryToEnterDim", at = @At("HEAD"))
    private static void distguard_bypassTickCountCheck(EntityTravelToDimensionEvent event, CallbackInfo ci) {
        Entity entity = event.getEntity();
        if (entity != null) {
            if (entity.tickCount < 300) {
                // Bypass the 15-second login cooldown that incorrectly shows the "Rebuilding" message
                entity.tickCount = 300;
            }

            // Check if traveling to a Cataclysm dimension and forcefully reset any active countdowns.
            if (event.getDimension() != null) {
                ResourceLocation dimLoc = event.getDimension().location();
                if (dimLoc != null && dimLoc.getNamespace().equals("cataclysm_dimension")) {
                    try {
                        Class<?> modClass = Class.forName("com.p1nero.cataclysm_dimension.CataclysmDimensionMod");
                        
                        Field integerMapField = modClass.getDeclaredField("RESOURCE_LOCATION_INTEGER_MAP");
                        integerMapField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        Map<ResourceLocation, Integer> resourceLocationIntegerMap = (Map<ResourceLocation, Integer>) integerMapField.get(null);
                        
                        Field booleanMapField = modClass.getDeclaredField("RESOURCE_KEY_BOOLEAN_MAP");
                        booleanMapField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        Map<ResourceLocation, Boolean> resourceKeyBooleanMap = (Map<ResourceLocation, Boolean>) booleanMapField.get(null);
                        
                        if (resourceLocationIntegerMap != null && resourceLocationIntegerMap.getOrDefault(dimLoc, 0) > 0) {
                            com.mojang.logging.LogUtils.getLogger().info("[ReverieServerFix] Player is trying to enter dimension {}. Proactively canceling the reset countdown!", dimLoc);
                            resourceLocationIntegerMap.put(dimLoc, 0);
                            if (resourceKeyBooleanMap != null) {
                                resourceKeyBooleanMap.put(dimLoc, false);
                            }
                        }
                    } catch (Exception e) {
                        com.mojang.logging.LogUtils.getLogger().error("[ReverieServerFix] Failed to reset Cataclysm countdown reflectively: ", e);
                    }
                }
            }
        }
    }
}
