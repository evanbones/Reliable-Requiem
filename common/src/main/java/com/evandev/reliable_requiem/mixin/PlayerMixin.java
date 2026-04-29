package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.CommonClass;
import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.api.IRequiemItem;
import com.evandev.reliable_requiem.config.ModConfig;
import com.evandev.reliable_requiem.modules.RequiemModules;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerKeptItems {

    @Unique
    private Map<Integer, ItemStack> reliableRequiem$keptItems = new HashMap<>();

    @Unique
    private BlockPos reliableRequiem$lastDeathPos;

    @Unique
    private ResourceKey<Level> reliableRequiem$lastDeathDimension;

    @Unique
    private String reliableRequiem$lastDamageSource = "";

    @Override
    public Map<Integer, ItemStack> reliableRequiem$getKeptItems() {
        return reliableRequiem$keptItems;
    }

    @Override
    public void reliableRequiem$setKeptItems(Map<Integer, ItemStack> items) {
        this.reliableRequiem$keptItems = items;
    }

    @Override
    public BlockPos reliableRequiem$getLastDeathPos() {
        return reliableRequiem$lastDeathPos;
    }

    @Override
    public void reliableRequiem$setLastDeathPos(BlockPos pos) {
        this.reliableRequiem$lastDeathPos = pos;
    }

    @Override
    public ResourceKey<Level> reliableRequiem$getLastDeathDimension() {
        return reliableRequiem$lastDeathDimension;
    }

    @Override
    public void reliableRequiem$setLastDeathDimension(ResourceKey<Level> dimension) {
        this.reliableRequiem$lastDeathDimension = dimension;
    }

    @Override
    public String reliableRequiem$getLastDamageSource() {
        return reliableRequiem$lastDamageSource;
    }

    @Override
    public void reliableRequiem$setLastDamageSource(String damageSourceId) {
        this.reliableRequiem$lastDamageSource = damageSourceId;
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDie(DamageSource cause, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        this.reliableRequiem$setLastDeathPos(player.blockPosition());
        this.reliableRequiem$setLastDeathDimension(player.level().dimension());
        this.reliableRequiem$setLastDamageSource(cause.getMsgId());
    }

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN")
    )
    private void reliableRequiem$tagDeathDrops(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity droppedItem = cir.getReturnValue();
        Player player = (Player) (Object) this;

        if (droppedItem != null && player.isDeadOrDying()) {
            ((IRequiemItem) droppedItem).reliableRequiem$setDroppedOnDeath(true);

            if (ModConfig.get().enabled && ModConfig.get().condenseDeathDrops) {
                droppedItem.setDeltaMovement(0, 0, 0);
            }
        }
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void onDropEquipment(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }

        Inventory inv = player.getInventory();
        Map<Integer, ItemStack> kept = new HashMap<>();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (EnchantmentHelper.hasVanishingCurse(stack)) {
                    continue;
                }

                if (!RequiemModules.processItemOnDeath(stack, serverPlayer, i)) {
                    if (!stack.isEmpty()) {
                        kept.put(i, stack.copy());
                    }
                    inv.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        reliableRequiem$setKeptItems(kept);
    }

    @Inject(method = "getExperienceReward", at = @At("RETURN"), cancellable = true)
    protected void onGetExperienceReward(CallbackInfoReturnable<Integer> cir) {
        Player player = (Player) (Object) this;
        int originalDrop = cir.getReturnValue();
        cir.setReturnValue(CommonClass.onExperienceDrop(player, originalDrop));
    }
}