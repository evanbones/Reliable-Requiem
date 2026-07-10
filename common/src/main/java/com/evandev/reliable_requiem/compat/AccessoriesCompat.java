package com.evandev.reliable_requiem.compat;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.modules.RequiemModules;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.events.OnDeathCallback;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

import java.util.Iterator;
import java.util.List;

/**
 * Accessories drops equipped accessories itself: it mixes into dropAllDeathLoot and runs its
 * own onDeath handling *before* Player#dropEquipment is ever called, so by the time our
 * PlayerMixin#onDropEquipment injection runs, its accessory containers are already emptied.
 * We hook its OnDeathCallback instead, which fires with the about-to-be-dropped stacks still
 * available, letting us pull the ones we want to keep out of that list before Accessories drops them.
 */
public class AccessoriesCompat {

    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;
        OnDeathCallback.EVENT.register(AccessoriesCompat::onDeath);
    }

    private static TriState onDeath(TriState currentState, LivingEntity entity, AccessoriesCapability capability, DamageSource source, List<ItemStack> droppedStacks) {
        if (!(entity instanceof ServerPlayer player)) return TriState.DEFAULT;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return TriState.DEFAULT;

        ListTag keptList = new ListTag();
        Iterator<ItemStack> iterator = droppedStacks.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.isEmpty()) continue;

            if (!RequiemModules.processItemOnDeath(stack, player, -1)) {
                iterator.remove();

                if (!stack.isEmpty()) {
                    CompoundTag tag = new CompoundTag();
                    tag.put("Item", stack.save(new CompoundTag()));
                    keptList.add(tag);
                }
            }
        }

        if (!keptList.isEmpty()) {
            CompoundTag keptAcc = ((IPlayerKeptItems) player).reliableRequiem$getKeptAccessories();
            if (keptAcc == null) {
                keptAcc = new CompoundTag();
            }
            keptAcc.put("Accessories", keptList);
            ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
        }

        return TriState.DEFAULT;
    }

    public static void restoreKeptAccessories(ServerPlayer player) {
        CompoundTag keptAcc = ((IPlayerKeptItems) player).reliableRequiem$getKeptAccessories();
        if (keptAcc == null || !keptAcc.contains("Accessories", Tag.TAG_LIST)) return;

        ListTag accessoriesList = keptAcc.getList("Accessories", Tag.TAG_COMPOUND);
        AccessoriesCapability capability = AccessoriesCapability.get(player);

        for (int i = 0; i < accessoriesList.size(); i++) {
            CompoundTag tag = accessoriesList.getCompound(i);
            ItemStack stack = ItemStack.of(tag.getCompound("Item"));
            if (stack.isEmpty()) continue;

            if (capability != null) {
                capability.attemptToEquipAccessory(stack);
            }

            if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }

        keptAcc.remove("Accessories");
        ((IPlayerKeptItems) player).reliableRequiem$setKeptAccessories(keptAcc);
    }
}
