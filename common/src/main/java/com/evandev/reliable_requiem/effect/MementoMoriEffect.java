package com.evandev.reliable_requiem.effect;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MementoMoriEffect extends MobEffect {

    private static final Identifier MOVEMENT_MOD_ID = Identifier.fromNamespaceAndPath("reliable_requiem", "memento_mori_movement");
    private static final Identifier DAMAGE_MOD_ID = Identifier.fromNamespaceAndPath("reliable_requiem", "memento_mori_damage");

    public MementoMoriEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A4A4A);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_MOD_ID, -0.20D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_MOD_ID, -2.0D, AttributeModifier.Operation.ADD_VALUE);
    }
}