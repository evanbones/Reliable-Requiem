package com.evandev.reliable_requiem.compat;

import com.rosemods.heart_crystals.core.other.HCEvents;
import com.rosemods.heart_crystals.core.other.HCPlayerInfo;
import net.minecraft.world.entity.player.Player;

public class HeartCrystalsCompat {

    public static void updateHeartCrystalsHealth(Player player, double maxHealth) {
        int heartCount = (int) Math.round(maxHealth / 2.0);
        HCPlayerInfo.setHeartSet(player, true);
        HCPlayerInfo.setHeartCount(player, heartCount);
        HCEvents.setMaxHealthAttribute(heartCount * 2, player);
    }
}
