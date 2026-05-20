package com.roddy.reverieserverfix.mixin;

import net.magister.bookofdragons.entity.base.dragon.DragonBase;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.magister.bookofdragons.entity.ai.pathfinding.DragonAsyncPathfinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Mixin targeted at BookOfDragons to prevent NullPointerExceptions during asynchronous pathfinding.
 * The mod sometimes passes a null target position when a dragon loses its path, causing the Server
 * blockable event loop to log a massive FATAL crash trace. This skips the pathing calculation if target is null.
 */
@Mixin(value = DragonAsyncPathfinder.class, remap = false)
public class BookOfDragonsMixin {

    @Inject(method = "calculatePathAsync", at = @At("HEAD"), cancellable = true, remap = false)
    private static void reverieserverfix_preventPathingNPE(DragonBase dragon, Vec3 target, boolean isFlying, Consumer<Path> callback, CallbackInfo ci) {
        if (target == null) {
            if (callback != null) {
                callback.accept(null);
            }
            ci.cancel();
        }
    }
}
