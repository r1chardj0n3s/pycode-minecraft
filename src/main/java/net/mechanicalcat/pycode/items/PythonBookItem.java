/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.mechanicalcat.pycode.items;

import net.mechanicalcat.pycode.PyCode;
import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.script.PythonCode;
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
import net.minecraftforge.fml.common.FMLLog;

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
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemstack, World world, EntityPlayer playerIn, EnumHand hand) {
        FMLLog.info("Book onItemRightClick remote=%s, stack=%s, hand=%s", world.isRemote, itemstack, hand);
        // don't activate the GUI if in offhand; don't do *anything*
        if (hand == EnumHand.OFF_HAND) return new ActionResult(EnumActionResult.FAIL, itemstack);

        PyCode.proxy.openBook(playerIn, itemstack);
        return new ActionResult(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return;
        if (compound.hasKey("title")) {
            String title = compound.getString("title");
            if (!title.isEmpty()) {
                tooltip.add(title);
            }
        }
    }

//    public static boolean isNBTValid(NBTTagCompound nbt) {
//        if (nbt == null) {
//            return false;
//        } else if (!nbt.hasKey("pages", 9)) {
//            return false;
//        } else {
//            NBTTagList nbttaglist = nbt.getTagList("pages", 8);
//            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
//                String s = nbttaglist.getStringTagAt(i);
//                if (s.length() > 32767) {
//                    return false;
//                }
//            }
//            return true;
//        }
//    }
}
