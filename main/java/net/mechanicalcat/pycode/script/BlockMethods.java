package net.mechanicalcat.pycode.script;


import com.google.common.collect.Lists;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.python.core.PyObject;

import java.util.List;

public class BlockMethods extends BaseMethods {
    private PyCodeBlockTileEntity block;

    public BlockMethods(PyCodeBlockTileEntity block, EntityPlayer player) {
        super(player);
        this.block = block;
    }

    // PyObject[] args, String[] keywords -- http://www.jython.org/archive/22/userfaq.html#supporting-args-and-kw-in-java-methods
    public void firework() {
        World world = this.block.getWorld();
        if (world.isRemote) return;

        ItemStack fireworkItem = new ItemStack(Items.FIREWORKS);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        nbttagcompound2.setBoolean("Flicker", true);
        List<Integer> list = Lists.<Integer>newArrayList();
        list.add(Integer.valueOf(ItemDye.DYE_COLORS[1]));
        int[] aint1 = new int[list.size()];
        for (int l2 = 0; l2 < aint1.length; ++l2) {
            aint1[l2] = ((Integer)list.get(l2)).intValue();
        }
        nbttagcompound2.setIntArray("Colors", aint1);
        nbttagcompound2.setByte("Type", (byte)1);
        nbttaglist.appendTag(nbttagcompound2);

        nbttagcompound1.setTag("Explosions", nbttaglist);
        nbttagcompound1.setByte("Flight", (byte)2);
        nbttagcompound.setTag("Fireworks", nbttagcompound1);
        fireworkItem.setTagCompound(nbttagcompound);

        BlockPos pos = this.block.getPos();
        Entity firework = new EntityFireworkRocket(world,
                pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
                fireworkItem);
        world.spawnEntityInWorld(firework);
    }
}


