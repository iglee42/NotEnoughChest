package fr.iglee42.notenoughchest.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class CustomChestBlock extends ChestBlock {
    private final int chestTypeIndex;

    public CustomChestBlock(Properties p_51490_, Supplier<BlockEntityType<? extends ChestBlockEntity>> p_51491_, int chestTypeIndex) {
        super(p_51490_, p_51491_);
        this.chestTypeIndex = chestTypeIndex;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153064_, BlockState p_153065_) {
        return new CustomChestBlockEntity(p_153064_,p_153065_,chestTypeIndex);
    }

}
