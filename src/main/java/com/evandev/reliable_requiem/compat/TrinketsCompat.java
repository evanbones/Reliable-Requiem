package com.evandev.reliable_requiem.compat;

//? if fabric && <26.1 {
/*import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.modules.RequiemModules;
import com.evandev.reliable_requiem.util.NbtHelper;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/^*
 * Kept trinkets are stored on the player and put back into their slots on respawn.
 ^/
public class TrinketsCompat {

    public static void handleAccessoryDeath(ServerPlayer player) {
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
                        trinketTag.put("Item", NbtHelper.saveStack(stack, player.level().registryAccess()));
                        keptTrinketsList.add(trinketTag);
                    }
                    inventory.setItem(index, ItemStack.EMPTY);
                } else {
                    RequiemModules.dropDeathItem(player, stack);
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
        if (keptAcc == null || !NbtHelper.hasList(keptAcc, "Trinkets")) return;

        ListTag trinketsList = NbtHelper.getList(keptAcc, "Trinkets");
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
            Map<String, Map<String, TrinketInventory>> inventoryMap = trinkets.getInventory();

            for (int i = 0; i < trinketsList.size(); i++) {
                CompoundTag trinketTag = NbtHelper.getCompound(trinketsList, i);
                String groupName = NbtHelper.getString(trinketTag, "Group");
                String slotName = NbtHelper.getString(trinketTag, "Slot");
                int index = NbtHelper.getInt(trinketTag, "Index");
                ItemStack stack = NbtHelper.loadStack(NbtHelper.getCompound(trinketTag, "Item"), player.level().registryAccess());

                if (stack.isEmpty()) continue;

                TrinketInventory inventory = null;
                Map<String, TrinketInventory> groupMap = inventoryMap.get(groupName);
                if (groupMap != null) {
                    inventory = groupMap.get(slotName);
                }

                if (inventory != null && index >= 0 && index < inventory.getContainerSize() && inventory.getItem(index).isEmpty()) {
                    inventory.setItem(index, stack);
                } else {
                    RequiemModules.returnItem(player, stack);
                }
            }
        });

        keptAcc.remove("Trinkets");
        ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
    }
}
*///?}
