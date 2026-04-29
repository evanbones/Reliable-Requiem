package com.evandev.reliable_requiem;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.effect.MementoMoriEffect;
import com.evandev.reliable_requiem.modules.ModEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class ReliableRequiem implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        ModEffects.MEMENTO_MORI = Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                new ResourceLocation(Constants.MOD_ID, "memento_mori"),
                new MementoMoriEffect()
        );

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            boolean wasDeath = !alive;

            CommonClass.onPlayerClone(oldPlayer, newPlayer, wasDeath);

            if (wasDeath) {
                Map<Integer, ItemStack> keptItems = ((IPlayerKeptItems) oldPlayer).reliableRequiem$getKeptItems();

                for (Map.Entry<Integer, ItemStack> entry : keptItems.entrySet()) {
                    int slot = entry.getKey();
                    ItemStack stack = entry.getValue();
                    newPlayer.getInventory().setItem(slot, stack);
                }
            }
        });
    }
}