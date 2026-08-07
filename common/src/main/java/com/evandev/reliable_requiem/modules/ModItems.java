package com.evandev.reliable_requiem.modules;

import com.evandev.reliable_requiem.Constants;
import com.evandev.reliable_requiem.item.CrystalHeartItem;
import com.evandev.reliable_requiem.registration.util.RegistrationProvider;
import com.evandev.reliable_requiem.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {

    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MOD_ID);

    public static final RegistryObject<Item> CRYSTAL_SHARD = ITEMS.register(
            "crystal_shard",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON))
    );

    public static final RegistryObject<Item> CRYSTAL_HEART = ITEMS.register(
            "crystal_heart",
            () -> new CrystalHeartItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(16))
    );

    public static void load() {
    }
}
