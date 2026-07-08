package com.evandev.reliable_requiem.compat;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.modules.RequiemModules;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class AccessoriesCompat {

    public static void handleAccessoryDeath(ServerPlayer player, String lastDamageSource) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) return;

        ListTag keptAccessoriesList = new ListTag();
        Map<String, AccessoriesContainer> containers = capability.getContainers();

        containers.forEach((slotName, container) -> {
            // Primary accessories
            processContainer(player, slotName, container.getAccessories(), false, keptAccessoriesList);
            // Cosmetic accessories
            processContainer(player, slotName, container.getCosmeticAccessories(), true, keptAccessoriesList);
        });

        if (!keptAccessoriesList.isEmpty()) {
            CompoundTag keptAcc = ((IPlayerKeptItems) player).reliableRequiem$getKeptAccessories();
            if (keptAcc == null) {
                keptAcc = new CompoundTag();
            }
            keptAcc.put("Accessories", keptAccessoriesList);
            ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
        }
    }

    private static void processContainer(ServerPlayer player, String slotName, ExpandedSimpleContainer simpleContainer, boolean cosmetic, ListTag keptList) {
        for (int i = 0; i < simpleContainer.getContainerSize(); i++) {
            ItemStack stack = simpleContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (!RequiemModules.processItemOnDeath(stack, player, -1)) {
                if (!stack.isEmpty()) {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("SlotName", slotName);
                    tag.putBoolean("Cosmetic", cosmetic);
                    tag.putInt("Index", i);
                    tag.put("Item", stack.save(player.registryAccess()));
                    keptList.add(tag);
                }
                simpleContainer.setItem(i, ItemStack.EMPTY);
            } else {
                player.drop(stack, true, false);
                simpleContainer.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public static void restoreKeptAccessories(ServerPlayer player) {
        CompoundTag keptAcc = ((IPlayerKeptItems) player).reliableRequiem$getKeptAccessories();
        if (keptAcc == null || !keptAcc.contains("Accessories", Tag.TAG_LIST)) return;

        ListTag accessoriesList = keptAcc.getList("Accessories", Tag.TAG_COMPOUND);
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            Map<String, AccessoriesContainer> containers = capability.getContainers();

            for (int i = 0; i < accessoriesList.size(); i++) {
                CompoundTag tag = accessoriesList.getCompound(i);
                String slotName = tag.getString("SlotName");
                boolean cosmetic = tag.getBoolean("Cosmetic");
                int index = tag.getInt("Index");
                ItemStack stack = ItemStack.parseOptional(player.registryAccess(), tag.getCompound("Item"));

                if (!stack.isEmpty()) {
                    AccessoriesContainer container = containers.get(slotName);
                    if (container != null) {
                        ExpandedSimpleContainer simpleContainer = cosmetic ? container.getCosmeticAccessories() : container.getAccessories();
                        if (simpleContainer != null && index >= 0 && index < simpleContainer.getContainerSize()) {
                            simpleContainer.setItem(index, stack);
                        }
                    }
                }
            }
        }

        keptAcc.remove("Accessories");
        ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
    }
}
