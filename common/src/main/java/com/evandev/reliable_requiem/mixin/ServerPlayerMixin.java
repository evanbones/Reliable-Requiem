package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.modules.RequiemModules;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void reliableRequiem$onSetRespawnPosition(ResourceKey<Level> dimension, BlockPos position, float angle, boolean forced, boolean sendMessage, CallbackInfo ci) {
        if (RequiemModules.onSetSpawn()) {
            ci.cancel();
        }
    }
}