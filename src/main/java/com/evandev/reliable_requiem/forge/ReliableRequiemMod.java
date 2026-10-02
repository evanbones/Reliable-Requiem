package com.evandev.reliable_requiem.forge;

//? if forge {
/*import com.evandev.reliable_requiem.CommonClass;
import com.evandev.reliable_requiem.Constants;
import com.evandev.reliable_requiem.forge.client.ClientConfigSetup;
import com.evandev.reliable_requiem.modules.ModEffects;
import com.evandev.reliable_requiem.modules.ModItems;
import com.evandev.reliable_requiem.platform.RegistryHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class ReliableRequiemMod {

    public ReliableRequiemMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEffects.load();
        ModItems.load();
        RegistryHelper.init(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ReliableRequiemMod::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ReliableRequiemMod::onPlayerRespawn);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register();
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CommonClass.init();
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer && event.getOriginal() instanceof ServerPlayer oldPlayer) {
            CommonClass.onPlayerClone(oldPlayer, newPlayer, event.isWasDeath());
        }
    }

    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CommonClass.onPlayerRespawn(player);
        }
    }
}
*///?}
