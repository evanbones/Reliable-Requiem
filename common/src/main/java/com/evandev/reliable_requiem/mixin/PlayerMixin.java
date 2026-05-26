package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.CommonClass;
import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.modules.RequiemModules;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    private void onDie(DamageSource source, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        this.reliableRequiem$setLastDeathPos(player.blockPosition());
        this.reliableRequiem$setLastDeathDimension(player.level().dimension());
        this.reliableRequiem$setLastDamageSource(source.getMsgId());
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void onDropEquipment(ServerLevel level, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            return;
        }

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
    private void reliableRequiem$saveAdditionalData(ValueOutput output, CallbackInfo ci) {
        if (!this.reliableRequiem$keptItems.isEmpty()) {
            Map<String, ItemStack> serializedMap = new HashMap<>();
            this.reliableRequiem$keptItems.forEach((slot, stack) -> serializedMap.put(String.valueOf(slot), stack));

            output.store("ReliableRequiem_KeptItems", Codec.unboundedMap(Codec.STRING, ItemStack.CODEC), serializedMap);
        }

        if (this.reliableRequiem$lastDeathPos != null) {
            output.store("ReliableRequiem_DeathPos", BlockPos.CODEC, this.reliableRequiem$lastDeathPos);
        }

        if (this.reliableRequiem$lastDeathDimension != null) {
            output.store("ReliableRequiem_DeathDim", ResourceKey.codec(Registries.DIMENSION), this.reliableRequiem$lastDeathDimension);
        }

        if (this.reliableRequiem$lastDamageSource != null && !this.reliableRequiem$lastDamageSource.isEmpty()) {
            output.store("ReliableRequiem_DamageSource", Codec.STRING, this.reliableRequiem$lastDamageSource);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void reliableRequiem$loadAdditionalData(ValueInput input, CallbackInfo ci) {
        this.reliableRequiem$keptItems.clear();

        input.read("ReliableRequiem_KeptItems", Codec.unboundedMap(Codec.STRING, ItemStack.CODEC))
                .ifPresent(map -> map.forEach((slotStr, stack) -> {
                    try {
                        this.reliableRequiem$keptItems.put(Integer.parseInt(slotStr), stack);
                    } catch (NumberFormatException ignored) {
                    }
                }));

        input.read("ReliableRequiem_DeathPos", BlockPos.CODEC)
                .ifPresent(pos -> this.reliableRequiem$lastDeathPos = pos);

        input.read("ReliableRequiem_DeathDim", ResourceKey.codec(Registries.DIMENSION))
                .ifPresent(dim -> this.reliableRequiem$lastDeathDimension = dim);

        input.read("ReliableRequiem_DamageSource", Codec.STRING)
                .ifPresent(source -> this.reliableRequiem$lastDamageSource = source);
    }

    @Inject(method = "getBaseExperienceReward", at = @At("RETURN"), cancellable = true)
    protected void onGetExperienceReward(ServerLevel level, CallbackInfoReturnable<Integer> cir) {
        Player player = (Player) (Object) this;
        int originalDrop = cir.getReturnValue();
        cir.setReturnValue(CommonClass.onExperienceDrop(player, originalDrop));
    }
}