package com.evandev.reliable_requiem.modules;

import com.evandev.reliable_requiem.Constants;
import com.evandev.reliable_requiem.effect.MementoMoriEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {
    public static Holder<MobEffect> MEMENTO_MORI;

    public static void init() {
        MobEffect effect = Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "memento_mori"),
                new MementoMoriEffect()
        );
        MEMENTO_MORI = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }
}