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
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nonnull;

public class PythonWandItem extends Item {
    public PythonWandItem() {
        setUnlocalizedName(Reference.PyCodeRegistrations.WAND.getUnlocalizedName());
        setRegistryName(Reference.PyCodeRegistrations.WAND.getRegistryName());
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemstack, World world, EntityPlayer player, EnumHand hand) {
        FMLLog.info("onItemRightClick %s", world.isRemote);

        if (world.isRemote) return new ActionResult(EnumActionResult.PASS, itemstack);
        WorldServer ws = (WorldServer)world;

        ItemStack offhand = player.getHeldItemOffhand();
        if (offhand == null) new ActionResult(EnumActionResult.PASS, itemstack);

        Item offitem = offhand.getItem();
        if (offitem instanceof PythonBookItem || offitem instanceof ItemWritableBook) {
            System.out.println("WE HAZ A BOOK!");
            NBTTagCompound nbt = itemstack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                itemstack.setTagCompound(nbt);
            }
            String code = PythonCode.bookAsString(offhand);
            if (code == null) {
                PythonCode.failz0r(ws, player.getPosition(), "Could not get pages from the book!?");
                return new ActionResult(EnumActionResult.FAIL, itemstack);
            }
            nbt.setString("code", code);
        }

        return new ActionResult(EnumActionResult.SUCCESS, itemstack);
    }
}
