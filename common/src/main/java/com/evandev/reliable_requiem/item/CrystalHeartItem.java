package com.evandev.reliable_requiem.item;

import com.evandev.reliable_requiem.config.ModConfig;
import com.evandev.reliable_requiem.platform.Services;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrystalHeartItem extends Item {

    public CrystalHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ModConfig config = ModConfig.get();

        AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            double currentMax = maxHealthAttr.getBaseValue();
            if (currentMax >= config.maxMaxHealth) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.reliable_requiem.max_health_limit_reached"), true);
                }
                return InteractionResultHolder.fail(itemStack);
            }

            if (!level.isClientSide()) {
                double newMax = Math.min(config.maxMaxHealth, currentMax + config.crystalHeartHealthAmount);
                maxHealthAttr.setBaseValue(newMax);
                Services.PLATFORM.updateHeartCrystalsHealth(player, newMax);
                player.heal((float) config.crystalHeartHealthAmount);

                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.TOTEM_USE,
                        SoundSource.PLAYERS,
                        0.8f,
                        2.6f
                );

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.HEART,
                            player.getX(),
                            player.getY() + 1.0,
                            player.getZ(),
                            12,
                            0.4,
                            0.5,
                            0.4,
                            0.1
                    );
                }

                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }

            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        }

        return InteractionResultHolder.pass(itemStack);
    }
}
