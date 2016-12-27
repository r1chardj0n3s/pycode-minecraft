package net.mechanicalcat.pycode.script;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;


public interface IHasPythonCode {
    public void initCode();
    public boolean handleItemInteraction(WorldServer world, EntityPlayer player, BlockPos pos, ItemStack heldItem);
}
