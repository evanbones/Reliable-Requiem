package com.evandev.reliable_requiem.effect;

import com.evandev.reliable_requiem.config.ModConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MementoMoriEffect extends MobEffect {

    public MementoMoriEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A4A4A);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "71056c28-2b81-43e9-a477-809623e1b764", ModConfig.get().mementoSpeedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "8b139031-1554-469b-8eab-8c9df410c538", ModConfig.get().mementoDamageModifier, AttributeModifier.Operation.ADDITION);
    }
}