package net.mechanicalcat.pycode.items;

import net.mechanicalcat.pycode.PyCode;
import net.mechanicalcat.pycode.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;


public class PythonBookItem extends Item {
    public List<String> pages;

    public PythonBookItem() {
        setUnlocalizedName(Reference.PyCodeRegistrations.BOOK.getUnlocalizedName());
        setRegistryName(Reference.PyCodeRegistrations.BOOK.getRegistryName());
        this.setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        PyCode.proxy.openBook(playerIn, itemStackIn);
        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    public static boolean isNBTValid(NBTTagCompound nbt) {
        if (nbt == null) {
            return false;
        } else if (!nbt.hasKey("pages", 9)) {
            return false;
        } else {
            NBTTagList nbttaglist = nbt.getTagList("pages", 8);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                String s = nbttaglist.getStringTagAt(i);
                if (s.length() > 32767) {
                    return false;
                }
            }
            return true;
        }
    }
}
