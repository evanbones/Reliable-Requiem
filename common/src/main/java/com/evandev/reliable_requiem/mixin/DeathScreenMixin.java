package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    protected DeathScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void reliableRequiem$onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (ModConfig.get().enabled && ModConfig.get().displayDeathCoordinates) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                BlockPos pos = ((IPlayerKeptItems) player).reliableRequiem$getLastDeathPos();
                if (pos == null) pos = player.blockPosition();

                Component deathCoordsText = Component.translatable(
                        "text.reliable_requiem.death_coords",
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                );

                graphics.centeredText(this.font, deathCoordsText, this.width / 2, 115, 0xFFFFFFFF);
            }
        }
    }
}