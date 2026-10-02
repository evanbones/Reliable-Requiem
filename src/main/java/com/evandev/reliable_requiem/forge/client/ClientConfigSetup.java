package com.evandev.reliable_requiem.forge.client;

//? if forge {
/*import com.evandev.reliable_requiem.config.YaclConfigIntegration;
import com.evandev.reliable_requiem.platform.Services;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ClientConfigSetup {
    public static void register() {
        if (!Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) return;
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> YaclConfigIntegration.createScreen(parent))
        );
    }
}
*///?}
