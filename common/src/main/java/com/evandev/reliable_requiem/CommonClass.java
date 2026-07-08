package com.evandev.reliable_requiem;

import com.evandev.reliable_requiem.config.ModConfig;
import com.evandev.reliable_requiem.modules.ModEffects;
import com.evandev.reliable_requiem.modules.RequiemModules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CommonClass {

    public static void init() {
        ModConfig.get();
    }

    public static void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wasDeath) {
        RequiemModules.onPlayerClone(oldPlayer, newPlayer, wasDeath);
    }

    public static void onPlayerRespawn(ServerPlayer player) {
        RequiemModules.onPlayerRespawn(player);
    }

    public static int onExperienceDrop(Player player, int originalDrop) {
        return RequiemModules.calculateDroppedExperience(player, originalDrop);
    }

    public static boolean checkItemDrop(ItemStack stack) {
        return RequiemModules.shouldDropItem(stack);
    }
}