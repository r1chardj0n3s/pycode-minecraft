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

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.script.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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

    static public boolean useItem(ItemStack itemstack, EntityPlayer player, WorldServer world, BlockPos pos) {
        ItemStack offhand = player.getHeldItemOffhand();
        if (offhand == null) return false;

        Item offitem = offhand.getItem();
        if (offitem instanceof PythonBookItem || offitem instanceof ItemWritableBook) {
            String content = PythonCode.bookAsString(offhand);
            if (content == null) {
                PythonCode.failz0r(world, player.getPosition(), "Could not get pages from the book!?");
                return true;
            }
            PythonCode code = new PythonCode();
            code.setCodeString(content);

            // the following will actually run the code; TODO maybe setContext could be better-named?
            code.setContext(world, player, player.getPosition());

            if (code.hasKey("invoke")) {
                RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
                MyBase interaction = null;
                if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
                    IBlockState block = world.getBlockState(target.getBlockPos());
                    interaction = new MyBlock(block, target.getBlockPos());
                } else if (target.typeOfHit == RayTraceResult.Type.ENTITY) {
                    if (target.entityHit instanceof EntityPlayer) {
                        interaction = new MyEntityPlayer((EntityPlayer)target.entityHit);
                    } else {
                        interaction = new MyEntity(target.entityHit);
                    }
                }
                code.invoke("invoke", interaction);
            }
            return true;
        }

        return false;
    }

}
