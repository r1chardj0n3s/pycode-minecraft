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

package net.mechanicalcat.pycode.events;

import net.mechanicalcat.pycode.PyCode;
import net.mechanicalcat.pycode.blocks.PythonBlock;
import net.mechanicalcat.pycode.entities.HandEntity;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.mechanicalcat.pycode.items.PythonWandItem;
import net.mechanicalcat.pycode.script.BlockMethods;
import net.mechanicalcat.pycode.script.MyEntity;
import net.mechanicalcat.pycode.script.MyEntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class PyCodeEventHandler {
    @SubscribeEvent
    public void onHandClick(PlayerInteractEvent.EntityInteractSpecific e){
        if (e.getWorld().isRemote) return;

        FMLLog.info("ENTITY INTERACT %s", e.getTarget());
        Entity entity = e.getTarget();

        if (entity instanceof HandEntity) {
            EntityPlayer player = e.getEntityPlayer();
            HandEntity h = (HandEntity) e.getTarget();
            e.setCanceled(h.handleItemInteraction(player, e.getItemStack(), e.getHand()));
        }
    }

    @SubscribeEvent
    public void onBlockClick(PlayerInteractEvent.RightClickBlock e){
        World world = e.getWorld();
        if (world.isRemote) return;

        EnumHand hand = e.getHand();
        if (hand == EnumHand.OFF_HAND) return;

        BlockPos pos = e.getPos();
        IBlockState state = e.getWorld().getBlockState(pos);

        if (!(state.getBlock() instanceof PythonBlock)) return;

        ItemStack stack = e.getItemStack();
        FMLLog.info("BLOCK INTERACT %s with %s", state, stack);

        e.setCanceled(PythonBlock.handleItemInteraction((WorldServer)world, pos, e.getEntityPlayer(), stack));
    }

    // this should be called only for air clicks
    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem e){
        World world = e.getWorld();
        if (world.isRemote) return;
        EnumHand hand = e.getHand();
        if (hand == EnumHand.OFF_HAND) return;

        ItemStack stack = e.getItemStack();
        if (stack == null) return;
        Item item = stack.getItem();
        FMLLog.info("ITEM INTERACT %s", stack);

        if (item instanceof PythonBookItem) {
            // can't do this here because it somehow screws up GUI mode, and there's no mouse pointer
//            PyCode.proxy.openBook(e.getEntityPlayer(), stack);
        } else if (item instanceof PythonWandItem) {
            PythonWandItem.useItem(stack, e.getEntityPlayer(), (WorldServer)world, e.getPos());
        }
    }
}
