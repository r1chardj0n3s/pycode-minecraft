package net.mechanicalcat.pycode.script;


import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockMethods extends BaseMethods {
    private PyCodeBlockTileEntity block;

    public BlockMethods(PyCodeBlockTileEntity block, EntityPlayer player) {
        super(player);
        this.block = block;
    }

    public void firework() {
        World world = this.block.getWorld();
        if (world.isRemote) return;

        BlockPos pos = this.block.getPos();
        Entity firework = new EntityFireworkRocket(world,
                pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
                new ItemStack(Items.GLOWSTONE_DUST));
        world.spawnEntityInWorld(firework);
    }
}
