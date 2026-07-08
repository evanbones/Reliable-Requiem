package com.evandev.reliable_requiem.platform;

import com.evandev.reliable_requiem.compat.CuriosCompat;
import com.evandev.reliable_requiem.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public void handleAccessoryDeath(ServerPlayer player, String lastDamageSource) {
        if (isModLoaded("curios")) {
            CuriosCompat.handleAccessoryDeath(player, lastDamageSource);
        }
    }

    @Override
    public void restoreKeptAccessories(ServerPlayer newPlayer) {
        if (isModLoaded("curios")) {
            CuriosCompat.restoreKeptAccessories(newPlayer);
        }
    }
}