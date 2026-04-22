package com.evandev.reliable_requiem.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IPlayerKeptItems {
    List<ItemStack> reliableRequiem$getKeptItems();

    void reliableRequiem$setKeptItems(List<ItemStack> items);

    BlockPos reliableRequiem$getLastDeathPos();

    void reliableRequiem$setLastDeathPos(BlockPos pos);

    ResourceKey<Level> reliableRequiem$getLastDeathDimension();

    void reliableRequiem$setLastDeathDimension(ResourceKey<Level> dimension);

    String reliableRequiem$getLastDamageSource();

    void reliableRequiem$setLastDamageSource(String damageSourceId);
}