package fr.iglee42.notenoughchest.chest;

import fr.iglee42.notenoughchest.NotEnoughChest;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CustomChestBlockEntity extends ChestBlockEntity {

    private int chestTypeIndex;

    public CustomChestBlockEntity(BlockPos p_155331_, BlockState p_155332_) {
        this(p_155331_, p_155332_,0);
    }
    public CustomChestBlockEntity(BlockPos p_155331_, BlockState p_155332_,int chestTypeIndex) {
        super(NotEnoughChest.CHEST.get(), p_155331_, p_155332_);
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
