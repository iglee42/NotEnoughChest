package fr.iglee42.notenoughchests.chest;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CustomChestBlockEntity extends ChestBlockEntity {

    private int chestTypeIndex;

    public CustomChestBlockEntity(BlockPos p_155331_, BlockState p_155332_) {
        this(p_155331_, p_155332_,0);
    }
    public CustomChestBlockEntity(BlockPos p_155331_, BlockState p_155332_,int chestTypeIndex) {
        super(NotEnoughChests.CHEST.get(), p_155331_, p_155332_);
        this.chestTypeIndex = chestTypeIndex;
    }

    public CustomChestBlockEntity(BlockEntityType<?> type,BlockPos p_155331_, BlockState p_155332_, int chestTypeIndex) {
        super(type, p_155331_, p_155332_);
        this.chestTypeIndex = chestTypeIndex;
    }

    public int getChestTypeIndex() {
        return chestTypeIndex == -1 ? 0 :chestTypeIndex;
    }

    @Override
    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }
}
