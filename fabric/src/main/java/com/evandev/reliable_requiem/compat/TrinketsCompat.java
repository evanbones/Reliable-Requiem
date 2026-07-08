package com.evandev.reliable_requiem.compat;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.modules.RequiemModules;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class TrinketsCompat {

    public static void handleAccessoryDeath(ServerPlayer player, String lastDamageSource) {
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
            ListTag keptTrinketsList = new ListTag();

            trinkets.forEach((slotRef, stack) -> {
                if (stack.isEmpty()) return;

                TrinketInventory inventory = slotRef.inventory();
                int index = slotRef.index();

                if (!RequiemModules.processItemOnDeath(stack, player, -1)) {
                    if (!stack.isEmpty()) {
                        CompoundTag trinketTag = new CompoundTag();
                        trinketTag.putString("Group", inventory.getSlotType().getGroup());
                        trinketTag.putString("Slot", inventory.getSlotType().getName());
                        trinketTag.putInt("Index", index);
                        trinketTag.put("Item", stack.save(new CompoundTag()));
                        keptTrinketsList.add(trinketTag);
                    }
                    inventory.setItem(index, ItemStack.EMPTY);
                } else {
                    player.drop(stack, true, false);
                    inventory.setItem(index, ItemStack.EMPTY);
                }
            });

            if (!keptTrinketsList.isEmpty()) {
                CompoundTag keptAcc = ((IPlayerKeptItems) player).reliableRequiem$getKeptAccessories();
                if (keptAcc == null) {
                    keptAcc = new CompoundTag();
                }
                keptAcc.put("Trinkets", keptTrinketsList);
                ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
            }
        });
    }

    public static void restoreKeptAccessories(ServerPlayer player) {
        CompoundTag keptAcc = ((IPlayerKeptItems) player).reliableRequiem$getKeptAccessories();
        if (keptAcc == null || !keptAcc.contains("Trinkets", Tag.TAG_LIST)) return;

        ListTag trinketsList = keptAcc.getList("Trinkets", Tag.TAG_COMPOUND);
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
            Map<String, Map<String, TrinketInventory>> inventoryMap = trinkets.getInventory();

            for (int i = 0; i < trinketsList.size(); i++) {
                CompoundTag trinketTag = trinketsList.getCompound(i);
                String groupName = trinketTag.getString("Group");
                String slotName = trinketTag.getString("Slot");
                int index = trinketTag.getInt("Index");
                ItemStack stack = ItemStack.of(trinketTag.getCompound("Item"));

                if (!stack.isEmpty()) {
                    Map<String, TrinketInventory> groupMap = inventoryMap.get(groupName);
                    if (groupMap != null) {
                        TrinketInventory inventory = groupMap.get(slotName);
                        if (inventory != null && index >= 0 && index < inventory.getContainerSize()) {
                            inventory.setItem(index, stack);
                        }
                    }
                }
            }
        });

        keptAcc.remove("Trinkets");
        ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
    }
}
