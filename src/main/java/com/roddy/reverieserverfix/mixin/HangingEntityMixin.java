package com.roddy.reverieserverfix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(HangingEntity.class)
public abstract class HangingEntityMixin {

    @Inject(method = "m_8119_", at = @At("HEAD"), cancellable = true, remap = false)
    private void distguard_discardInvalidHangingEntity(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide) {
            return;
        }

        if (!distguard_survives(self)) {
            self.discard();
            ci.cancel();
        }
    }

    private static boolean distguard_survives(Entity entity) {
        try {
            Method survivesMethod = HangingEntity.class.getDeclaredMethod("survives");
            survivesMethod.setAccessible(true);
            return (Boolean) survivesMethod.invoke(entity);
        } catch (Exception ignored) {
            return true;
        }
    }
}