package com.evandev.reliable_requiem.platform;

import com.evandev.reliable_requiem.compat.AccessoriesCompat;
import com.evandev.reliable_requiem.compat.CuriosCompat;
import com.evandev.reliable_requiem.compat.HeartCrystalsCompat;
import com.evandev.reliable_requiem.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
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
        if (isModLoaded("accessories")) {
            AccessoriesCompat.restoreKeptAccessories(newPlayer);
        }
        if (isModLoaded("curios")) {
            CuriosCompat.restoreKeptAccessories(newPlayer);
        }
    }

    @Override
    public void updateHeartCrystalsHealth(Player player, double maxHealth) {
        if (isModLoaded("heart_crystals")) {
            HeartCrystalsCompat.updateHeartCrystalsHealth(player, maxHealth);
        }
    }
}