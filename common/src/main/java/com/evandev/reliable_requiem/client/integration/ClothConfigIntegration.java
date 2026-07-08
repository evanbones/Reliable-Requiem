package com.evandev.reliable_requiem.client.integration;

import com.evandev.reliable_requiem.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ModConfig config = ModConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.reliable_requiem.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.general"));
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.enabled"), config.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.reliable_requiem.enabled.tooltip"))
                .setSaveConsumer(val -> config.enabled = val).build());

        ConfigCategory hunger = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.hunger"));
        hunger.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.keepFood"), config.keepFood)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.keepFood.tooltip"))
                .setSaveConsumer(val -> config.keepFood = val).build());
        hunger.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.keepSaturation"), config.keepSaturation)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.keepSaturation.tooltip"))
                .setSaveConsumer(val -> config.keepSaturation = val).build());
        hunger.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.minFood"), config.minFood)
                .setDefaultValue(0).setMin(0).setMax(20)
                .setTooltip(Component.translatable("config.reliable_requiem.minFood.tooltip"))
                .setSaveConsumer(val -> config.minFood = val).build());
        hunger.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.maxFood"), config.maxFood)
                .setDefaultValue(20).setMin(0).setMax(20)
                .setTooltip(Component.translatable("config.reliable_requiem.maxFood.tooltip"))
                .setSaveConsumer(val -> config.maxFood = val).build());

        ConfigCategory experience = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.experience"));
        experience.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.lostXpPercent"), config.lostXpPercent)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.lostXpPercent.tooltip"))
                .setSaveConsumer(val -> config.lostXpPercent = val).build());
        experience.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.droppedXpPerLevel"), config.droppedXpPerLevel)
                .setDefaultValue(7).setMin(0)
                .setTooltip(Component.translatable("config.reliable_requiem.droppedXpPerLevel.tooltip"))
                .setSaveConsumer(val -> config.droppedXpPerLevel = val).build());
        experience.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.maxDroppedXp"), config.maxDroppedXp)
                .setDefaultValue(100).setMin(0)
                .setTooltip(Component.translatable("config.reliable_requiem.maxDroppedXp.tooltip"))
                .setSaveConsumer(val -> config.maxDroppedXp = val).build());

        ConfigCategory inventory = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.inventory"));
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.keepMainInventoryChance"), config.keepMainInventoryChance)
                .setDefaultValue(0.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.keepMainInventoryChance.tooltip"))
                .setSaveConsumer(val -> config.keepMainInventoryChance = val).build());
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.keepHotbarChance"), config.keepHotbarChance)
                .setDefaultValue(0.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.keepHotbarChance.tooltip"))
                .setSaveConsumer(val -> config.keepHotbarChance = val).build());
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.keepArmorChance"), config.keepArmorChance)
                .setDefaultValue(0.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.keepArmorChance.tooltip"))
                .setSaveConsumer(val -> config.keepArmorChance = val).build());
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.keepOffhandChance"), config.keepOffhandChance)
                .setDefaultValue(0.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.keepOffhandChance.tooltip"))
                .setSaveConsumer(val -> config.keepOffhandChance = val).build());
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.keepAccessoriesChance"), config.keepAccessoriesChance)
                .setDefaultValue(0.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.keepAccessoriesChance.tooltip"))
                .setSaveConsumer(val -> config.keepAccessoriesChance = val).build());
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.keepDurabilityLoss"), config.keepDurabilityLoss)
                .setDefaultValue(0.10).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.keepDurabilityLoss.tooltip"))
                .setSaveConsumer(val -> config.keepDurabilityLoss = val).build());
        inventory.addEntry(entryBuilder.startStrList(Component.translatable("config.reliable_requiem.bypassDamageSources"), config.bypassKeepInventoryDamageSources)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.reliable_requiem.bypassDamageSources.tooltip"))
                .setSaveConsumer(val -> config.bypassKeepInventoryDamageSources = val)
                .build());
        inventory.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.defaultDestroyChance"), config.defaultDestroyChance)
                .setDefaultValue(0.0).setMin(0.0).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.defaultDestroyChance.tooltip"))
                .setSaveConsumer(val -> config.defaultDestroyChance = val).build());
        inventory.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.dropDespawnTime"), config.dropDespawnTime)
                .setDefaultValue(300).setMin(0)
                .setTooltip(Component.translatable("config.reliable_requiem.dropDespawnTime.tooltip"))
                .setSaveConsumer(val -> config.dropDespawnTime = val).build());
        inventory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.neverDespawnDeathDrops"), config.neverDespawnDeathDrops)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.neverDespawnDeathDrops.tooltip"))
                .setSaveConsumer(val -> config.neverDespawnDeathDrops = val).build());
        inventory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.condenseDeathDrops"), config.condenseDeathDrops)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.condenseDeathDrops.tooltip"))
                .setSaveConsumer(val -> config.condenseDeathDrops = val).build());
        inventory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.explosionResistantDeathDrops"), config.explosionResistantDeathDrops)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.explosionResistantDeathDrops.tooltip"))
                .setSaveConsumer(val -> config.explosionResistantDeathDrops = val).build());
        inventory.addEntry(entryBuilder.startStrList(Component.translatable("config.reliable_requiem.keptItemOverrides"), config.keptItemOverrides)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.reliable_requiem.keptItemOverrides.tooltip"))
                .setSaveConsumer(val -> config.keptItemOverrides = val).build());
        inventory.addEntry(entryBuilder.startStrList(Component.translatable("config.reliable_requiem.droppedItemOverrides"), config.droppedItemOverrides)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.reliable_requiem.droppedItemOverrides.tooltip"))
                .setSaveConsumer(val -> config.droppedItemOverrides = val).build());
        inventory.addEntry(entryBuilder.startStrList(Component.translatable("config.reliable_requiem.destroyedItemOverrides"), config.destroyedItemOverrides)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.reliable_requiem.destroyedItemOverrides.tooltip"))
                .setSaveConsumer(val -> config.destroyedItemOverrides = val).build());
        inventory.addEntry(entryBuilder.startStrList(Component.translatable("config.reliable_requiem.bypassKeepInventoryDimensions"), config.bypassKeepInventoryDimensions)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.reliable_requiem.bypassKeepInventoryDimensions.tooltip"))
                .setSaveConsumer(val -> config.bypassKeepInventoryDimensions = val).build());

        ConfigCategory effects = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.effects"));
        effects.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.keepPositiveEffects"), config.keepPositiveEffects)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.keepPositiveEffects.tooltip"))
                .setSaveConsumer(val -> config.keepPositiveEffects = val).build());
        effects.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.keepNegativeEffects"), config.keepNegativeEffects)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.keepNegativeEffects.tooltip"))
                .setSaveConsumer(val -> config.keepNegativeEffects = val).build());

        ConfigCategory healthAndSpawn = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.health_and_respawning"));
        healthAndSpawn.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.scaleRespawnHealth"), config.scaleRespawnHealth)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.scaleRespawnHealth.tooltip"))
                .setSaveConsumer(val -> config.scaleRespawnHealth = val).build());
        healthAndSpawn.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.respawnHealthPercent"), config.respawnHealthPercent)
                .setDefaultValue(1.0).setMin(0.1).setMax(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.respawnHealthPercent.tooltip"))
                .setSaveConsumer(val -> config.respawnHealthPercent = val).build());
        healthAndSpawn.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.maxRespawnHealth"), config.maxRespawnHealth)
                .setDefaultValue(20.0).setMin(1.0)
                .setTooltip(Component.translatable("config.reliable_requiem.maxRespawnHealth.tooltip"))
                .setSaveConsumer(val -> config.maxRespawnHealth = val).build());
        healthAndSpawn.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.respawnRandomRadius"), config.respawnRandomRadius)
                .setDefaultValue(0).setMin(0)
                .setTooltip(Component.translatable("config.reliable_requiem.respawnRandomRadius.tooltip"))
                .setSaveConsumer(val -> config.respawnRandomRadius = val).build());

        ConfigCategory memento = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.memento_mori"));
        memento.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.applyMementoMori"), config.applyMementoMori)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.applyMementoMori.tooltip"))
                .setSaveConsumer(val -> config.applyMementoMori = val).build());
        memento.addEntry(entryBuilder.startIntField(Component.translatable("config.reliable_requiem.mementoDuration"), config.mementoDuration)
                .setDefaultValue(600).setMin(0)
                .setTooltip(Component.translatable("config.reliable_requiem.mementoDuration.tooltip"))
                .setSaveConsumer(val -> config.mementoDuration = val).build());
        memento.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.mementoSpeedModifier"), config.mementoSpeedModifier)
                .setDefaultValue(-0.20)
                .setTooltip(Component.translatable("config.reliable_requiem.mementoSpeedModifier.tooltip"))
                .setSaveConsumer(val -> config.mementoSpeedModifier = val).build());
        memento.addEntry(entryBuilder.startDoubleField(Component.translatable("config.reliable_requiem.mementoDamageModifier"), config.mementoDamageModifier)
                .setDefaultValue(-2.0)
                .setTooltip(Component.translatable("config.reliable_requiem.mementoDamageModifier.tooltip"))
                .setSaveConsumer(val -> config.mementoDamageModifier = val).build());

        ConfigCategory misc = builder.getOrCreateCategory(Component.translatable("config.reliable_requiem.category.miscellaneous"));
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.restrictRespawning"), config.restrictRespawning)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.reliable_requiem.restrictRespawning.tooltip"))
                .setSaveConsumer(val -> config.restrictRespawning = val).build());
        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.reliable_requiem.displayDeathCoordinates"), config.displayDeathCoordinates)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.reliable_requiem.displayDeathCoordinates.tooltip"))
                .setSaveConsumer(val -> config.displayDeathCoordinates = val).build());

        return builder.build();
    }
}