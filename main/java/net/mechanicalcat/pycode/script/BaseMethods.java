package net.mechanicalcat.pycode.script;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseMethods {
    protected EntityPlayer player;
    protected World world;

    protected BaseMethods(World world, EntityPlayer player) {
        this.world = world;
        this.player = player;
    }

    public void chat(String message) {
        this.player.addChatComponentMessage(new TextComponentString(message));
    }

    public void water(BlockPos pos) {
        if (this.world.isRemote) return;

        Block b = this.world.getBlockState(pos).getBlock();

        if (this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
        }
    }

    public void lava(BlockPos pos) {
        if (this.world.isRemote) return;

        Block b = this.world.getBlockState(pos).getBlock();

        if (this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.FLOWING_LAVA.getDefaultState());
        }
    }

    public void clear(BlockPos pos) {
        if (this.world.isRemote) return;

        Block b = this.world.getBlockState(pos).getBlock();

        if (this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

}
