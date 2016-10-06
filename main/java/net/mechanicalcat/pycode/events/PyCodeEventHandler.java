package net.mechanicalcat.pycode.events;

import net.mechanicalcat.pycode.entities.TurtleEntity;
import net.mechanicalcat.pycode.script.IHasPythonCode;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.script.ScriptException;

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
//            if (code_block.handleInteraction(world, player, pos, event.getItemStack())) {
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
//            if (((IHasPythonCode) other).handleInteraction(world, event.getEntityPlayer(), other.getPosition(), event.getItemStack())) {
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
