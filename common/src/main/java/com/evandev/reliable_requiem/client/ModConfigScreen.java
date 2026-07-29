package com.evandev.reliable_requiem.client;

import com.evandev.reliable_requiem.config.ModConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModConfigScreen {
    public static Screen createScreen(Screen parent) {
        ModConfig.load();
        ModConfig config = ModConfig.get();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.reliable_requiem.title"))
                .save(ModConfig::save);

        ConfigCategory.Builder general = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.general"))
                .option(createBoolOption("enabled", true, () -> config.enabled, val -> config.enabled = val));

        ConfigCategory.Builder hunger = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.hunger"))
                .option(createBoolOption("keepFood", false, () -> config.keepFood, val -> config.keepFood = val))
                .option(createBoolOption("keepSaturation", false, () -> config.keepSaturation, val -> config.keepSaturation = val))
                .option(createIntOption("minFood", 0, 0, 20, () -> config.minFood, val -> config.minFood = val))
                .option(createIntOption("maxFood", 20, 0, 20, () -> config.maxFood, val -> config.maxFood = val));

        ConfigCategory.Builder experience = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.experience"))
                .option(createDoubleOption("lostXpPercent", 1.0, 0.0, 1.0, () -> config.lostXpPercent, val -> config.lostXpPercent = val))
                .option(createIntOption("droppedXpPerLevel", 7, 0, Integer.MAX_VALUE, () -> config.droppedXpPerLevel, val -> config.droppedXpPerLevel = val))
                .option(createIntOption("maxDroppedXp", 100, 0, Integer.MAX_VALUE, () -> config.maxDroppedXp, val -> config.maxDroppedXp = val));

        ConfigCategory.Builder inventory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.inventory"))
                .option(createDoubleOption("keepMainInventoryChance", 0.0, 0.0, 1.0, () -> config.keepMainInventoryChance, val -> config.keepMainInventoryChance = val))
                .option(createDoubleOption("keepHotbarChance", 0.0, 0.0, 1.0, () -> config.keepHotbarChance, val -> config.keepHotbarChance = val))
                .option(createDoubleOption("keepArmorChance", 0.0, 0.0, 1.0, () -> config.keepArmorChance, val -> config.keepArmorChance = val))
                .option(createDoubleOption("keepOffhandChance", 0.0, 0.0, 1.0, () -> config.keepOffhandChance, val -> config.keepOffhandChance = val))
                .option(createDoubleOption("keepDurabilityLoss", 0.10, 0.0, 1.0, () -> config.keepDurabilityLoss, val -> config.keepDurabilityLoss = val))
                .option(createDoubleOption("defaultDestroyChance", 0.0, 0.0, 1.0, () -> config.defaultDestroyChance, val -> config.defaultDestroyChance = val))
                .option(createIntOption("dropDespawnTime", 300, 0, Integer.MAX_VALUE, () -> config.dropDespawnTime, val -> config.dropDespawnTime = val))
                .option(createBoolOption("neverDespawnDeathDrops", false, () -> config.neverDespawnDeathDrops, val -> config.neverDespawnDeathDrops = val))
                .option(createBoolOption("condenseDeathDrops", false, () -> config.condenseDeathDrops, val -> config.condenseDeathDrops = val))
                .option(createBoolOption("explosionResistantDeathDrops", false, () -> config.explosionResistantDeathDrops, val -> config.explosionResistantDeathDrops = val))
                .group(createStringListOption("bypassDamageSources", () -> config.bypassKeepInventoryDamageSources, val -> config.bypassKeepInventoryDamageSources = val))
                .group(createStringListOption("keptItemOverrides", () -> config.keptItemOverrides, val -> config.keptItemOverrides = val))
                .group(createStringListOption("droppedItemOverrides", () -> config.droppedItemOverrides, val -> config.droppedItemOverrides = val))
                .group(createStringListOption("destroyedItemOverrides", () -> config.destroyedItemOverrides, val -> config.destroyedItemOverrides = val))
                .group(createStringListOption("bypassKeepInventoryDimensions", () -> config.bypassKeepInventoryDimensions, val -> config.bypassKeepInventoryDimensions = val));

        ConfigCategory.Builder effects = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.effects"))
                .option(createBoolOption("keepPositiveEffects", false, () -> config.keepPositiveEffects, val -> config.keepPositiveEffects = val))
                .option(createBoolOption("keepNegativeEffects", false, () -> config.keepNegativeEffects, val -> config.keepNegativeEffects = val));

        ConfigCategory.Builder healthAndSpawn = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.health_and_respawning"))
                .option(createBoolOption("scaleRespawnHealth", false, () -> config.scaleRespawnHealth, val -> config.scaleRespawnHealth = val))
                .option(createDoubleOption("respawnHealthPercent", 1.0, 0.1, 1.0, () -> config.respawnHealthPercent, val -> config.respawnHealthPercent = val))
                .option(createDoubleOption("maxRespawnHealth", 20.0, 1.0, Double.MAX_VALUE, () -> config.maxRespawnHealth, val -> config.maxRespawnHealth = val))
                .option(createIntOption("respawnRandomRadius", 0, 0, Integer.MAX_VALUE, () -> config.respawnRandomRadius, val -> config.respawnRandomRadius = val));

        ConfigCategory.Builder memento = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.memento_mori"))
                .option(createBoolOption("applyMementoMori", false, () -> config.applyMementoMori, val -> config.applyMementoMori = val))
                .option(createIntOption("mementoDuration", 600, 0, Integer.MAX_VALUE, () -> config.mementoDuration, val -> config.mementoDuration = val))
                .option(createDoubleOption("mementoSpeedModifier", -0.20, -1.0, 1.0, () -> config.mementoSpeedModifier, val -> config.mementoSpeedModifier = val))
                .option(createDoubleOption("mementoDamageModifier", -2.0, -100.0, 100.0, () -> config.mementoDamageModifier, val -> config.mementoDamageModifier = val));

        ConfigCategory.Builder misc = ConfigCategory.createBuilder()
                .name(Component.translatable("config.reliable_requiem.category.miscellaneous"))
                .option(createBoolOption("restrictRespawning", false, () -> config.restrictRespawning, val -> config.restrictRespawning = val))
                .option(createBoolOption("displayDeathCoordinates", true, () -> config.displayDeathCoordinates, val -> config.displayDeathCoordinates = val));

        return builder
                .category(general.build())
                .category(hunger.build())
                .category(experience.build())
                .category(inventory.build())
                .category(effects.build())
                .category(healthAndSpawn.build())
                .category(memento.build())
                .category(misc.build())
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> createBoolOption(String name, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Component.translatable("config.reliable_requiem." + name))
                .description(OptionDescription.of(Component.translatable("config.reliable_requiem." + name + ".tooltip")))
                .binding(defaultValue, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Integer> createIntOption(String name, int defaultValue, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
                .name(Component.translatable("config.reliable_requiem." + name))
                .description(OptionDescription.of(Component.translatable("config.reliable_requiem." + name + ".tooltip")))
                .binding(defaultValue, getter, setter)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(min).max(max))
                .build();
    }

    private static Option<Double> createDoubleOption(String name, double defaultValue, double min, double max, Supplier<Double> getter, Consumer<Double> setter) {
        return Option.<Double>createBuilder()
                .name(Component.translatable("config.reliable_requiem." + name))
                .description(OptionDescription.of(Component.translatable("config.reliable_requiem." + name + ".tooltip")))
                .binding(defaultValue, getter, setter)
                .controller(opt -> DoubleFieldControllerBuilder.create(opt).min(min).max(max))
                .build();
    }

    private static OptionGroup createStringListOption(String name, Supplier<List<String>> getter, Consumer<List<String>> setter) {
        return ListOption.<String>createBuilder()
                .name(Component.translatable("config.reliable_requiem." + name))
                .description(OptionDescription.of(Component.translatable("config.reliable_requiem." + name + ".tooltip")))
                .binding(new ArrayList<>(), getter, val -> setter.accept(new ArrayList<>(val)))
                .controller(StringControllerBuilder::create)
                .initial("")
                .build();
    }
}
