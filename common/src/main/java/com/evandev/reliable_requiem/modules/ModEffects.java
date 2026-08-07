package com.evandev.reliable_requiem.modules;

import com.evandev.reliable_requiem.Constants;
import com.evandev.reliable_requiem.effect.MementoMoriEffect;
import com.evandev.reliable_requiem.registration.util.RegistrationProvider;
import com.evandev.reliable_requiem.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {

    public static final RegistrationProvider<MobEffect> MOB_EFFECTS = RegistrationProvider.get(Registries.MOB_EFFECT, Constants.MOD_ID);

    public static final RegistryObject<MobEffect> MEMENTO_MORI = MOB_EFFECTS.register(
            "memento_mori",
            MementoMoriEffect::new
    );

    public static void load() {
    }
}