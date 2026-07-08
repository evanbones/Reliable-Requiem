package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.CommonClass;
import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.api.IRequiemItem;
import com.evandev.reliable_requiem.config.ModConfig;
import com.evandev.reliable_requiem.modules.RequiemModules;
import com.evandev.reliable_requiem.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
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

    @Unique
    private CompoundTag reliableRequiem$keptAccessories = new CompoundTag();

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

    @Override
    public CompoundTag reliableRequiem$getKeptAccessories() {
        return this.reliableRequiem$keptAccessories;
    }

    @Override
    public void reliableRequiem$setKeptAccessories(CompoundTag tag) {
        this.reliableRequiem$keptAccessories = tag;
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDie(net.minecraft.world.damagesource.DamageSource source, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        this.reliableRequiem$setLastDeathPos(player.blockPosition());
        this.reliableRequiem$setLastDeathDimension(player.level().dimension());
        this.reliableRequiem$setLastDamageSource(source.getMsgId());
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

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void onDropEquipment(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }

        Services.PLATFORM.handleAccessoryDeath(serverPlayer, this.reliableRequiem$lastDamageSource);

        Inventory inv = player.getInventory();
        Map<Integer, ItemStack> kept = new HashMap<>();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {

                if (EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                    inv.setItem(i, ItemStack.EMPTY);
                    continue;
                }

                if (!RequiemModules.processItemOnDeath(stack, serverPlayer, i)) {
                    if (!stack.isEmpty()) {
                        kept.put(i, stack.copy());
                    } else {
                        inv.setItem(i, ItemStack.EMPTY);
                    }
                } else {
                    player.drop(stack, true, false);
                    inv.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        reliableRequiem$setKeptItems(kept);

        ci.cancel();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void reliableRequiem$saveAdditionalData(CompoundTag compound, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        ListTag keptItemsList = new ListTag();
        for (Map.Entry<Integer, ItemStack> entry : this.reliableRequiem$keptItems.entrySet()) {
            CompoundTag slotTag = new CompoundTag();
            slotTag.putInt("Slot", entry.getKey());
            Tag itemTag = entry.getValue().saveOptional(player.registryAccess());
            slotTag.put("Item", itemTag);
            keptItemsList.add(slotTag);
        }
        compound.put("ReliableRequiem_KeptItems", keptItemsList);

        if (this.reliableRequiem$keptAccessories != null && !this.reliableRequiem$keptAccessories.isEmpty()) {
            compound.put("ReliableRequiem_KeptAccessories", this.reliableRequiem$keptAccessories);
        }

        if (this.reliableRequiem$lastDeathPos != null) {
            compound.putLong("ReliableRequiem_DeathPos", this.reliableRequiem$lastDeathPos.asLong());
        }
        if (this.reliableRequiem$lastDeathDimension != null) {
            compound.putString("ReliableRequiem_DeathDim", this.reliableRequiem$lastDeathDimension.location().toString());
        }
        if (this.reliableRequiem$lastDamageSource != null) {
            compound.putString("ReliableRequiem_DamageSource", this.reliableRequiem$lastDamageSource);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void reliableRequiem$loadAdditionalData(CompoundTag compound, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        this.reliableRequiem$keptItems.clear();
        if (compound.contains("ReliableRequiem_KeptItems", Tag.TAG_LIST)) {
            ListTag listTag = compound.getList("ReliableRequiem_KeptItems", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag slotTag = listTag.getCompound(i);
                int slot = slotTag.getInt("Slot");
                ItemStack stack = ItemStack.parseOptional(player.registryAccess(), slotTag.getCompound("Item"));
                if (!stack.isEmpty()) {
                    this.reliableRequiem$keptItems.put(slot, stack);
                }
            }
        }

        if (compound.contains("ReliableRequiem_KeptAccessories", Tag.TAG_COMPOUND)) {
            this.reliableRequiem$keptAccessories = compound.getCompound("ReliableRequiem_KeptAccessories");
        } else {
            this.reliableRequiem$keptAccessories = new CompoundTag();
        }

        if (compound.contains("ReliableRequiem_DeathPos")) {
            this.reliableRequiem$lastDeathPos = BlockPos.of(compound.getLong("ReliableRequiem_DeathPos"));
        }
        if (compound.contains("ReliableRequiem_DeathDim")) {
            this.reliableRequiem$lastDeathDimension = ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.parse(compound.getString("ReliableRequiem_DeathDim"))
            );
        }
        if (compound.contains("ReliableRequiem_DamageSource")) {
            this.reliableRequiem$lastDamageSource = compound.getString("ReliableRequiem_DamageSource");
        }
    }

    @Inject(method = "getBaseExperienceReward", at = @At("RETURN"), cancellable = true)
    protected void onGetExperienceReward(CallbackInfoReturnable<Integer> cir) {
        Player player = (Player) (Object) this;
        int originalDrop = cir.getReturnValue();
        cir.setReturnValue(CommonClass.onExperienceDrop(player, originalDrop));
    }
}