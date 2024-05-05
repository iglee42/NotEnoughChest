package fr.iglee42.notenoughchests.chest;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CustomTrappedChestBlockEntity extends CustomChestBlockEntity {
    public CustomTrappedChestBlockEntity(BlockPos p_155862_, BlockState p_155863_) {
        super(NotEnoughChests.TRAPPED_CHEST.get(), p_155862_, p_155863_,0);
    }
    public CustomTrappedChestBlockEntity(BlockPos p_155862_, BlockState p_155863_,int chestTypeIndex) {
        super(NotEnoughChests.TRAPPED_CHEST.get(), p_155862_, p_155863_,chestTypeIndex);
    }

    protected void signalOpenCount(Level p_155865_, BlockPos p_155866_, BlockState p_155867_, int p_155868_, int p_155869_) {
        super.signalOpenCount(p_155865_, p_155866_, p_155867_, p_155868_, p_155869_);
        if (p_155868_ != p_155869_) {
            Block block = p_155867_.getBlock();
            p_155865_.updateNeighborsAt(p_155866_, block);
            p_155865_.updateNeighborsAt(p_155866_.below(), block);
        }

    }
}
