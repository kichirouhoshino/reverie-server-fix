package com.roddy.reverieserverfix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.p1nero.tcrcore.events.PlayerEventListeners", remap = false)
public class PlayerEventListenersMixin {

    @Inject(method = "onPlayerTryToEnterDim", at = @At("HEAD"))
    private static void distguard_bypassTickCountCheck(EntityTravelToDimensionEvent event, CallbackInfo ci) {
        Entity entity = event.getEntity();
        if (entity != null && entity.tickCount < 300) {
            // Bypass the 15-second login cooldown that incorrectly shows the "Rebuilding" message
            entity.tickCount = 300;
        }
    }
}
