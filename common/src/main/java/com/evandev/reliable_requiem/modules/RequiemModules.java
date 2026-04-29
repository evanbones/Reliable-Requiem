package com.evandev.reliable_requiem.modules;

import com.evandev.reliable_requiem.Constants;
import com.evandev.reliable_requiem.api.IPlayerKeptItems;
import com.evandev.reliable_requiem.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class RequiemModules {

    public static final TagKey<Enchantment> SOULBOUND_TAG = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("c", "soulbound"));
    public static final TagKey<Item> RETAINED_ON_DEATH_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MOD_ID, "retained_on_death"));
    private static final Random RAND = new Random();

    public static void onPlayerClone(ServerPlayer original, ServerPlayer newPlayer, boolean wasDeath) {
        if (!wasDeath || !ModConfig.get().enabled) return;
        ModConfig config = ModConfig.get();

        // Hunger Module
        FoodData oldFood = original.getFoodData();
        FoodData newFood = newPlayer.getFoodData();
        int foodLevel = config.keepFood ? oldFood.getFoodLevel() : 20;
        newFood.setFoodLevel(Math.max(config.minFood, Math.min(config.maxFood, foodLevel)));
        if (config.keepSaturation) newFood.setSaturation(oldFood.getSaturationLevel());

        // Effects Module
        if (config.keepPositiveEffects || config.keepNegativeEffects) {
            for (MobEffectInstance effectInstance : original.getActiveEffects()) {
                boolean isHarmful = effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL;
                if ((isHarmful && config.keepNegativeEffects) || (!isHarmful && config.keepPositiveEffects)) {
                    if (effectInstance.getEffect().equals(ModEffects.MEMENTO_MORI)) continue;
                    newPlayer.addEffect(new MobEffectInstance(effectInstance));
                }
            }
        }

        // Experience Module
        if (config.lostXpPercent < 1.0) {
            int totalOriginalPoints = getExperiencePoints(original);
            int keptPoints = totalOriginalPoints - (int) (totalOriginalPoints * config.lostXpPercent);
            newPlayer.setExperienceLevels(0);
            newPlayer.setExperiencePoints(0);
            newPlayer.giveExperiencePoints(keptPoints);
        }

        // Memento Mori
        if (config.applyMementoMori) {
            newPlayer.addEffect(new MobEffectInstance(ModEffects.MEMENTO_MORI, config.mementoDuration));
        }

        // Health Module
        double maxHealth = newPlayer.getMaxHealth();
        double targetHealth = config.scaleRespawnHealth ? (maxHealth * config.respawnHealthPercent) : maxHealth;
        targetHealth = Math.min(targetHealth, config.maxRespawnHealth);
        newPlayer.setHealth((float) Math.max(1.0, targetHealth));

        // Randomized Spawn Module
        if (config.respawnRandomRadius > 0 && newPlayer.level() instanceof ServerLevel serverLevel) {
            BlockPos currentPos = newPlayer.blockPosition();
            double offsetX = (RAND.nextDouble() * 2 - 1) * config.respawnRandomRadius;
            double offsetZ = (RAND.nextDouble() * 2 - 1) * config.respawnRandomRadius;

            int newX = currentPos.getX() + (int) offsetX;
            int newZ = currentPos.getZ() + (int) offsetZ;

            int newY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, newX, newZ);
            newPlayer.teleportTo(newX + 0.5, newY + 0.1, newZ + 0.5);
        }
    }

    public static boolean onSetSpawn() {
        return ModConfig.get().enabled && ModConfig.get().restrictRespawning;
    }

    public static boolean processItemOnDeath(ItemStack stack, ServerPlayer player, int slotIndex) {
        if (!ModConfig.get().enabled || stack.isEmpty()) return true;
        ModConfig config = ModConfig.get();

        String currentDimension = player.level().dimension().location().toString();
        if (config.bypassKeepInventoryDimensions.contains(currentDimension)) {
            return true;
        }

        String lastDamageSource = ((IPlayerKeptItems) player).reliableRequiem$getLastDamageSource();
        if (lastDamageSource != null && config.bypassKeepInventoryDamageSources.contains(lastDamageSource)) {
            return true;
        }

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (config.destroyedItemOverrides.contains(itemId)) {
            stack.shrink(stack.getCount());
            return false;
        }
        if (config.droppedItemOverrides.contains(itemId)) {
            return true;
        }
        boolean forceKeep = config.keptItemOverrides.contains(itemId);

        boolean hasSoulbound = false;
        var enchantments = EnchantmentHelper.getEnchantments(stack);
        for (Enchantment enchantment : enchantments.keySet()) {
            if (BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment).is(SOULBOUND_TAG)) {
                hasSoulbound = true;
                break;
            }
        }

        double keepChance = 0.0;
        if (slotIndex >= 0 && slotIndex <= 8) {
            keepChance = config.keepHotbarChance;
        } else if (slotIndex >= 9 && slotIndex <= 35) {
            keepChance = config.keepMainInventoryChance;
        } else if (slotIndex >= 36 && slotIndex <= 39) {
            keepChance = config.keepArmorChance;
        } else if (slotIndex == 40) {
            keepChance = config.keepOffhandChance;
        }

        boolean hasRetainedTag = stack.is(RETAINED_ON_DEATH_TAG);

        if (forceKeep || hasSoulbound || hasRetainedTag || (keepChance > 0 && RAND.nextDouble() < keepChance)) {
            if (stack.isDamageableItem() && config.keepDurabilityLoss > 0) {
                int damageAmount = (int) (stack.getMaxDamage() * config.keepDurabilityLoss);

                stack.hurtAndBreak(damageAmount, player, (brokenItem) -> {
                });
            }
            return false;
        }

        if (config.defaultDestroyChance > 0 && RAND.nextDouble() < config.defaultDestroyChance) {
            stack.shrink(stack.getCount());
            return false;
        }

        return true;
    }

    public static int calculateDroppedExperience(Player player, int originalDrop) {
        if (!ModConfig.get().enabled) return originalDrop;
        ModConfig config = ModConfig.get();
        int dropped = player.experienceLevel * config.droppedXpPerLevel;
        return Math.min(dropped, config.maxDroppedXp);
    }

    public static boolean shouldDropItem(ItemStack stack) {
        if (!ModConfig.get().enabled || stack.isEmpty()) return true;
        var enchantments = EnchantmentHelper.getEnchantments(stack);
        for (Enchantment enchantment : enchantments.keySet()) {
            if (BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment).is(SOULBOUND_TAG)) return false;
        }
        return true;
    }

    private static int getExperiencePoints(Player player) {
        return Math.round(player.experienceProgress * player.getXpNeededForNextLevel())
                + getExperiencePointsFromLevel(player.experienceLevel);
    }

    private static int getExperiencePointsFromLevel(int levels) {
        if (levels <= 16) return levels * levels + 6 * levels;
        if (levels <= 31) return (int) (levels * levels * 2.5 - levels * 40.5 + 360);
        return (int) (levels * levels * 4.5 - levels * 162.5 + 2220);
    }

    private static int getLevelsFromExperiencePoints(int points) {
        if (points < 394) return (int) (-3 + Math.sqrt(points + 9));
        if (points < 1628) return (int) (8.1 + 0.1 * Math.sqrt(40 * points - 7839));
        return (int) (18.0556 + 0.0555556 * Math.sqrt(72 * points - 54215));
    }
}