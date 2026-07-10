package com.evandev.reliable_requiem.platform;

import com.evandev.reliable_requiem.compat.AccessoriesCompat;
import com.evandev.reliable_requiem.compat.TrinketsCompat;
import com.evandev.reliable_requiem.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public void handleAccessoryDeath(ServerPlayer player, String lastDamageSource) {
        // Accessories handles its own death-drop logic via AccessoriesCompat's OnDeathCallback hook,
        // registered in CommonClass#init - its containers are already emptied by the time this fires.
        if (isModLoaded("trinkets")) {
            TrinketsCompat.handleAccessoryDeath(player, lastDamageSource);
        }
    }

    @Override
    public void restoreKeptAccessories(ServerPlayer newPlayer) {
        if (isModLoaded("accessories")) {
            AccessoriesCompat.restoreKeptAccessories(newPlayer);
        }
        if (isModLoaded("trinkets")) {
            TrinketsCompat.restoreKeptAccessories(newPlayer);
        }
    }
}