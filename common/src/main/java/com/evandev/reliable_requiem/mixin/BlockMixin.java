package com.evandev.reliable_requiem.mixin;

import com.evandev.reliable_requiem.config.ModConfig;
import com.evandev.reliable_requiem.modules.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void reliableRequiem$onSpawnerPlayerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (!level.isClientSide() && state.getBlock() instanceof SpawnerBlock && player != null && !player.isCreative()) {
            ModConfig config = ModConfig.get();
            if (config.enabled && config.enableSpawnerShardDrop) {
                if (level.getRandom().nextDouble() < config.spawnerShardDropChance) {
                    int count = Math.max(1, config.spawnerShardDropCount);
                    Block.popResource(level, pos, new ItemStack(ModItems.CRYSTAL_SHARD.get(), count));
                }
            }
        }
    }
}
