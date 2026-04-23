package com.evandev.reliable_requiem.effect;

import com.evandev.reliable_requiem.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MementoMoriEffect extends MobEffect {

    private static final ResourceLocation MOVEMENT_MOD_ID = ResourceLocation.fromNamespaceAndPath("reliable_requiem", "memento_mori_movement");
    private static final ResourceLocation DAMAGE_MOD_ID = ResourceLocation.fromNamespaceAndPath("reliable_requiem", "memento_mori_damage");

    public MementoMoriEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A4A4A);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_MOD_ID, ModConfig.get().mementoSpeedModifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_MOD_ID, ModConfig.get().mementoDamageModifier, AttributeModifier.Operation.ADD_VALUE);
    }
}