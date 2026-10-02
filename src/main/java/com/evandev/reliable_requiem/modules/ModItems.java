package com.evandev.reliable_requiem.modules;

import com.evandev.reliable_requiem.item.CrystalHeartItem;
import com.evandev.reliable_requiem.platform.RegistryHelper;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<Item> CRYSTAL_SHARD = RegistryHelper.registerItem(
            "crystal_shard",
            properties -> new Item(properties.rarity(Rarity.UNCOMMON))
    );

    public static final Supplier<Item> CRYSTAL_HEART = RegistryHelper.registerItem(
            "crystal_heart",
            properties -> new CrystalHeartItem(properties.rarity(Rarity.RARE).stacksTo(16))
    );

    public static void load() {
        RegistryHelper.registerToTab(CreativeModeTabs.INGREDIENTS, CRYSTAL_SHARD);
        RegistryHelper.registerToTab(CreativeModeTabs.INGREDIENTS, CRYSTAL_HEART);
    }
}
