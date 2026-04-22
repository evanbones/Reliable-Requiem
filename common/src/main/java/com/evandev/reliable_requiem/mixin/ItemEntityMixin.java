package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.api.IRequiemItem;
import com.evandev.reliable_requiem.config.ModConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements IRequiemItem {

    @Shadow
    private int age;

    @Unique
    private boolean reliableRequiem$droppedOnDeath = false;

    @Override
    public boolean reliableRequiem$isDroppedOnDeath() {
        return reliableRequiem$droppedOnDeath;
    }

    @Override
    public void reliableRequiem$setDroppedOnDeath(boolean droppedOnDeath) {
        this.reliableRequiem$droppedOnDeath = droppedOnDeath;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void reliableRequiem$onTick(CallbackInfo ci) {
        ItemEntity item = (ItemEntity) (Object) this;
        if (!item.level().isClientSide() && ModConfig.get().enabled && this.reliableRequiem$droppedOnDeath) {
            int configuredTimeInTicks = ModConfig.get().dropDespawnTime * 20;
            if (configuredTimeInTicks > 0 && this.age >= configuredTimeInTicks) {
                item.discard();
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void reliableRequiem$onSave(ValueOutput output, CallbackInfo ci) {
        if (this.reliableRequiem$droppedOnDeath) {
            output.putBoolean("ReliableRequiem_DeathDrop", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void reliableRequiem$onRead(ValueInput input, CallbackInfo ci) {
        this.reliableRequiem$droppedOnDeath = input.getBooleanOr("ReliableRequiem_DeathDrop", false);
    }
}