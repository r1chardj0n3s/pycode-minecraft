package net.mechanicalcat.pycode.items;

import net.mechanicalcat.pycode.blocks.PythonBlock;
import net.mechanicalcat.pycode.script.PythonCode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;


public class PythonBlockItem extends ItemBlock {
    public PythonBlockItem(PythonBlock block) {super(block);}

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return;
        if (compound.hasKey(PythonCode.CODE_NBT_TAG)) tooltip.add("[has code]");
    }
}
