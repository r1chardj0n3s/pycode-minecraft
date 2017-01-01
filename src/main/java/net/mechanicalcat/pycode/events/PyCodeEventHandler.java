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

public class PyCodeEventHandler {
//    @SubscribeEvent
//    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
//        if (event.isCanceled()) {
//            return;
//        }
//        World world = event.getWorld();
//        if (!world.isRemote) {
//            return;
//        }
//
//        // not a block?
//        if (event.getFace() == null || event.getPos() == null) {
//            return;
//        }
//
//        BlockPos pos = event.getPos();
//        EntityPlayer player = event.getEntityPlayer();
//        Block block = world.getBlockState(pos).getBlock();
//        TileEntity entity = world.getTileEntity(pos);
//
//        System.out.println("CLICK ON: " + block.getUnlocalizedName());
//        if (entity instanceof PyCodeBlockTileEntity) {
//            PyCodeBlockTileEntity code_block = (PyCodeBlockTileEntity) entity;
//            if (code_block.handleItemInteraction(world, player, pos, event.getItemStack())) {
//                event.setCanceled(true);
//            }
//        }
//    }
//
//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
//        if (event.isCanceled()) {
//            return;
//        }
//
//        World world = event.getWorld();
//        if (!world.isRemote) {
//            return;
//        }
//
//        Entity other = event.getTarget();
//        if (other instanceof IHasPythonCode) {
//            if (((IHasPythonCode) other).handleItemInteraction(world, event.getEntityPlayer(), other.getPosition(), event.getItemStack())) {
//                event.setCanceled(true);
//                event.setResult(Event.Result.DENY);
//            }
//        }
//    }

// https://github.com/diesieben07/Modjam-4/blob/master/src/main/java/mod/badores/event/FMLEventHandler.java#L62
//    private static final String NBT_KEY = "badores.firstjoin";

//    @SubscribeEvent
//    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
//        if (BadOres.config.isStartingBookDisabled()) {
//            return;
//        }
//
//        NBTTagCompound data = event.player.getEntityData();
//        NBTTagCompound persistent;
//        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
//            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
//        } else {
//            persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
//        }
//
//        if (!persistent.hasKey(NBT_KEY)) {
//            persistent.setBoolean(NBT_KEY, true);
//            event.player.inventory.addItemStackToInventory(new ItemStack(BadOres.badOreBook));
//        }
//    }
}
