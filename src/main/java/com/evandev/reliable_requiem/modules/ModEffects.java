package com.evandev.reliable_requiem.modules;

import com.evandev.reliable_requiem.effect.MementoMoriEffect;
import com.evandev.reliable_requiem.platform.RegistryHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

//? if >=1.21 {
import net.minecraft.core.Holder;
//?} else {
/*import java.util.function.Supplier;
*///?}

public class ModEffects {

    //? if >=1.21 {
    public static final Holder<MobEffect> MEMENTO_MORI = RegistryHelper.registerEffect("memento_mori", MementoMoriEffect::new);
    //?} else {
    /*public static final Supplier<MobEffect> MEMENTO_MORI = RegistryHelper.registerEffect("memento_mori", MementoMoriEffect::new);
    *///?}

    public static MobEffectInstance mementoMori(int duration) {
        //? if >=1.21 {
        return new MobEffectInstance(MEMENTO_MORI, duration);
        //?} else {
        /*return new MobEffectInstance(MEMENTO_MORI.get(), duration);
        *///?}
    }

    public static boolean isMementoMori(MobEffectInstance instance) {
        //? if >=1.21 {
        return instance.getEffect().value() == MEMENTO_MORI.value();
        //?} else {
        /*return instance.getEffect() == MEMENTO_MORI.get();
        *///?}
    }

    public static void load() {
    }
}
