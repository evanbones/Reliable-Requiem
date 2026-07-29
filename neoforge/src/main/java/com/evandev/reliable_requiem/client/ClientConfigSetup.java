package com.evandev.reliable_requiem.client;

import com.evandev.reliable_requiem.platform.Services;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClientConfigSetup {
    public static void register(ModContainer container) {
        if (Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> ModConfigScreen.createScreen(parent));
        }
    }
}