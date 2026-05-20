package com.roddy.reverieserverfix.mixin;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin targeted at Epic Fight Nightfall (com.hm.efn).
 *
 * EFN's EffekUnits.VFXENABLE() method improperly returns true on dedicated servers
 * if the 'aaa_particles' mod is installed on the server. Because it returns true,
 * the server attempts to execute client-side VFX methods (e.g., BurstBlueEffek, Stone_1_Effek)
 * which throws a RuntimeDistCleaner ExceptionInInitializerError/NoClassDefFoundError and
 * hangs the thread, triggering massive stack trace spam in Forge's EventBus.
 *
 * This mixin intercepts VFXENABLE() and forces it to return false on the server,
 * preventing the crashes at the root cause with zero performance overhead.
 */
@Mixin(targets = "com.hm.efn.util.EffekUnits", remap = false)
public class EffekUnitsMixin {

    @Inject(method = "VFXENABLE", at = @At("HEAD"), cancellable = true, remap = false)
    private static void distguard_vfxEnable(CallbackInfoReturnable<Boolean> cir) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            cir.setReturnValue(false);
        }
    }
}
