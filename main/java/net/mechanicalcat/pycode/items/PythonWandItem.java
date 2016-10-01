package net.mechanicalcat.pycode.items;

import net.mechanicalcat.pycode.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class PythonWandItem extends Item{
    public PythonWandItem() {
        setUnlocalizedName(Reference.PyCodeItems.WAND.getUnlocalizedName());
        setRegistryName(Reference.PyCodeItems.WAND.getRegistryName());
        setCreativeTab(CreativeTabs.TOOLS);
    }
}
