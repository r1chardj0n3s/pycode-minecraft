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
}
