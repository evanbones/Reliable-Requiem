package com.evandev.reliable_requiem.client;

import com.evandev.reliable_requiem.client.integration.ClothConfigIntegration;
import com.evandev.reliable_requiem.platform.Services;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModContainer;

public class ClientConfigSetup {
    public static void register(ModContainer container) {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> ClothConfigIntegration.createScreen(parent)));
        }
    }
}