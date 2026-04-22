package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.api.IRequiemItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean isDeadOrDying();

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN")
    )
    private void reliableRequiem$tagDeathDrops(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity droppedItem = cir.getReturnValue();

        if (droppedItem != null && (Object) this instanceof Player && this.isDeadOrDying()) {
            ((IRequiemItem) droppedItem).reliableRequiem$setDroppedOnDeath(true);
        }
    }
}