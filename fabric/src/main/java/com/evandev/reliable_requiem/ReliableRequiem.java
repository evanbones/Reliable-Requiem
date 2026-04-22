package com.evandev.reliable_requiem;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ReliableRequiem implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            boolean wasDeath = !alive;

            CommonClass.onPlayerClone(oldPlayer, newPlayer, wasDeath);

            if (wasDeath) {
                List<ItemStack> keptItems = ((IPlayerKeptItems) oldPlayer).reliableRequiem$getKeptItems();
                for (ItemStack stack : keptItems) {
                    newPlayer.getInventory().add(stack);
                }
            }
        });
    }
}