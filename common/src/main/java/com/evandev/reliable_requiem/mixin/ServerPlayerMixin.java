package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.modules.RequiemModules;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void reliableRequiem$onSetRespawnPosition(ServerPlayer.RespawnConfig respawnConfig, boolean showMessage, CallbackInfo ci) {
        if (RequiemModules.onSetSpawn()) {
            ci.cancel();
        }
    }
}