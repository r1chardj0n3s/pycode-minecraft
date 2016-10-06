package net.mechanicalcat.pycode.script;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public interface IHasPythonCode {
    public void initCode();
    public boolean handleInteraction(World world, EntityPlayer player, BlockPos pos, ItemStack heldItem);
}
