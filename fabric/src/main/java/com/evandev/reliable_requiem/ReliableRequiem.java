package com.evandev.reliable_requiem;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.modules.ModEffects;
import com.evandev.reliable_requiem.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class ReliableRequiem implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        ModEffects.init();

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            boolean wasDeath = !alive;

            CommonClass.onPlayerClone(oldPlayer, newPlayer, wasDeath);

            if (wasDeath) {
                CompoundTag keptAcc = ((IPlayerKeptItems) oldPlayer).reliableRequiem$getKeptAccessories();
                ((IPlayerKeptItems) newPlayer).reliableRequiem$setKeptAccessories(keptAcc);

                Map<Integer, ItemStack> keptItems = ((IPlayerKeptItems) oldPlayer).reliableRequiem$getKeptItems();

                for (Map.Entry<Integer, ItemStack> entry : keptItems.entrySet()) {
                    int slot = entry.getKey();
                    ItemStack stack = entry.getValue();
                    newPlayer.getInventory().setItem(slot, stack);
                }

                Services.PLATFORM.restoreKeptAccessories(newPlayer);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            CommonClass.onPlayerRespawn(newPlayer);
        });
    }
}