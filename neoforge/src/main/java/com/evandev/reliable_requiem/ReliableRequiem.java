package com.evandev.reliable_requiem;

import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.client.ClientConfigSetup;
import com.evandev.reliable_requiem.modules.ModEffects;
import com.evandev.reliable_requiem.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

@Mod(Constants.MOD_ID)
public class ReliableRequiem {
    public ReliableRequiem(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegister);

        NeoForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(modContainer);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CommonClass.init();
    }

    private void onRegister(final RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.MOB_EFFECT)) {
            ModEffects.init();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer && event.getOriginal() instanceof ServerPlayer oldPlayer) {

            CommonClass.onPlayerClone(oldPlayer, newPlayer, event.isWasDeath());

            if (event.isWasDeath()) {
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
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CommonClass.onPlayerRespawn(player);
        }
    }
}