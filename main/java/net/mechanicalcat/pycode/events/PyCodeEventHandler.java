package net.mechanicalcat.pycode.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PyCodeEventHandler {
    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            // event was on the server, ignore
            return;
        }

        // not sure I need this??
        if (event.isCanceled()) {
            return;
        }

        // not a block?
        if (event.getFace() == null || event.getPos() == null) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        Block block = world.getBlockState(event.getPos()).getBlock();

        System.out.println("CLICK ON: " + block.getUnlocalizedName());
    }
}
