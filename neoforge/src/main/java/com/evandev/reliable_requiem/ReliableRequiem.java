package com.evandev.reliable_requiem;

import com.evandev.reliable_requiem.client.ClientConfigSetup;
import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

@Mod(Constants.MOD_ID)
public class ReliableRequiem {
    public ReliableRequiem(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        if (FMLEnvironment.getDist().isClient()) {
            ClientConfigSetup.register(modContainer);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CommonClass.init();
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer && event.getOriginal() instanceof ServerPlayer oldPlayer) {

            CommonClass.onPlayerClone(oldPlayer, newPlayer, event.isWasDeath());

            if (event.isWasDeath()) {
                List<ItemStack> keptItems = ((IPlayerKeptItems) oldPlayer).reliableRequiem$getKeptItems();
                for (ItemStack stack : keptItems) {
                    newPlayer.getInventory().add(stack);
                }
            }
        }
    }
}