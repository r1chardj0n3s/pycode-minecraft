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

package net.mechanicalcat.pycode.script;


import com.google.common.collect.Lists;
import net.mechanicalcat.pycode.entities.EntityEnum;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.GameData;
import org.python.core.Py;
import org.python.core.PyObject;

import java.util.List;

public class BlockMethods extends BaseMethods {
    private PyCodeBlockTileEntity block;

    public BlockMethods(PyCodeBlockTileEntity block, EntityPlayer player) {
        super(block.getWorld(), player);
        this.block = block;
    }
    protected World getWorld() {return null;}

    public boolean isPowered() { return this.block.isPowered; }

    // PyObject[] args, String[] keywords -- http://www.jython.org/archive/22/userfaq.html#supporting-args-and-kw-in-java-methods
    public void firework() {
        if (this.world.isRemote) return;

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
        Entity firework = new EntityFireworkRocket(this.world,
                pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
                fireworkItem);
        this.world.spawnEntityInWorld(firework);
    }

    public void spawn(String entityName) throws EntityNameError {
        EntityEnum entityType = EntityEnum.getByName(entityName);
        if (entityType == null) {
            throw new EntityNameError(entityName);
        }
        if (this.world.isRemote) return;
        entityType.spawn(this.world, this.block.getPos().add(0.5, 1.0, 0.5));
    }

    // this is just a little crazypants
    private String[] s(String ... strings) {
        return strings;
    }

    public void put(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("put", s("pos", "blockname"), PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        BlockPos pos = mpos.blockPos;
        EnumFacing facing = EnumFacing.NORTH;
        IBlockState state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer)this.world);
        this.put(pos, state, facing);
    }

    public void alter(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("put", s("pos"), PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        BlockPos pos = mpos.blockPos;
        IBlockState state = this.world.getBlockState(pos);
        EnumFacing facing = PyRegistry.getBlockFacing(state);
        IBlockState modified = PyRegistry.modifyBlockStateFromSpec(state, r, facing);
        if (state != modified) {
            this.world.setBlockState(pos, modified);
        }
    }

    public void clear(MyBlockPos pos) {
        this.clear(pos.blockPos);
    }
    public void clear(BlockPos pos) {
        if (this.world == null || this.world.isRemote) return;
        Block b = this.world.getBlockState(pos).getBlock();
        if (!this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public void line(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("pos", "distance", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        ShapeGen.line(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }

    public void ladder(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("pos", "height", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        ShapeGen.ladder(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }

    public void floor(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("pos", "width", "depth", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        ShapeGen.floor(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }

    public void wall(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("pos", "depth", "height", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        ShapeGen.wall(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }

    public void cube(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("pos", "width", "depth", "height", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        ShapeGen.cube(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }

    public void circle(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("pos", "radius", "blockname"),
                // TODO PyRegistry.BLOCK_VARIATIONS
                s("color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        ShapeGen.circle(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }

    public void roof(PyObject[] args, String[] kws) throws BlockTypeError {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("roof", s("pos", "width", "depth", "blockname"),
                // TODO PyRegistry.BLOCK_VARIATIONS
                s("style", "color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        MyBlockPos mpos = (MyBlockPos)r.get("pos").__tojava__(MyBlockPos.class);
        RoofGen.roof(r, this.world, mpos.blockPos, EnumFacing.NORTH);
    }
}
