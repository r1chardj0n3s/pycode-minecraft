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
import net.mechanicalcat.pycode.net.InvokeWandMessage;
import net.mechanicalcat.pycode.net.ModNetwork;
import net.mechanicalcat.pycode.script.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PythonWandItem extends Item {
    public PythonWandItem() {
        setUnlocalizedName(Reference.PyCodeRegistrations.WAND.getUnlocalizedName());
        setRegistryName(Reference.PyCodeRegistrations.WAND.getRegistryName());
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.setFire(4);
        return true;
    }

    @Nullable
    static private PythonCode getCodeFromBook(EntityPlayer player) {
        ItemStack offhand = player.getHeldItemOffhand();
        if (offhand == null) {
            FMLLog.info("... nothing in off hand so pass");
            return null;
        }

        Item offitem = offhand.getItem();
        if (offitem instanceof PythonBookItem || offitem instanceof ItemWritableBook) {
            String content = PythonCode.bookAsString(offhand);
            if (content == null) {
                PythonCode.failz0r(player.worldObj, player.getPosition(), "Could not get pages from the book!?");
                return null;
            }

            PythonCode code = new PythonCode();
            code.setCodeString(content);

            // the following will actually run the code; TODO maybe setContext could be better-named?
            code.setContext(player.worldObj, player, player.getPosition());

            return code;
        }
        return null;
    }

    public static void invokeOnBlock(EntityPlayer player, BlockPos pos) {
        PythonCode code = getCodeFromBook(player);
        if (code == null) return;
        IBlockState state = player.worldObj.getBlockState(pos);
        if (code.hasKey("invoke")) code.invoke("invoke", new MyBlock(state, pos));
    }

    public EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos,
                                      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world == null || world.isRemote) return EnumActionResult.SUCCESS;
        FMLLog.info("Wand onItemUse stack=%s, hand=%s", itemstack, hand);
        invokeOnBlock(player, pos);
        return EnumActionResult.SUCCESS;
    }

    static public void invokeOnEntity(EntityPlayer player, Entity entity) {
        FMLLog.info("Wand invokeOnEntity%s, entity=%s", player, entity);
        PythonCode code = getCodeFromBook(player);
        if (code == null) return ;
        if (code.hasKey("invoke")) code.invoke("invoke", PyRegistry.myWrapper(player.worldObj, entity));
    }

    static public void invokeInDirection(EntityPlayer player, Vec3d vec) {
        PythonCode code = getCodeFromBook(player);
        if (code == null) return ;
        code.invoke("invoke", null);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemstack, World world,
                                                    EntityPlayer player, EnumHand hand) {
        FMLLog.info("Wand onItemRightClick stack=%s, hand=%s", itemstack, hand);
        if (world.isRemote) {
            // figure out what we're looking at and send it to the server
            RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
            ModNetwork.network.sendToServer(new InvokeWandMessage(target));
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
    }
}
