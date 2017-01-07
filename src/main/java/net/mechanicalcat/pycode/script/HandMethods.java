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

import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import org.python.core.Py;
import org.python.core.PyObject;


public class HandMethods extends BaseMethods {
    private HandEntity hand;

    public HandMethods(HandEntity hand, EntityPlayer player) {
        super(hand.getEntityWorld(), player);
        this.hand = hand;
    }

    public PyObject remember() {
        return new HandStateContextManager(this.hand);
    }

    public void forward() {
        this.forward(1);
    }
    public void forward(float distance) {
        this.hand.moveForward(distance);
    }

    public void back() {
        this.back(1);
    }
    public void back(float distance) {
        this.hand.moveForward(-distance);
    }

    public void sidle() {
        this.sidle(1);
    }
    public void sidle(float distance) {
        Vec3d pos = this.hand.getPositionVector();
        float rotation = this.hand.rotationYaw - 90;
        float f1 = -MathHelper.sin(rotation * 0.017453292F);
        float f2 = MathHelper.cos(rotation * 0.017453292F);
        pos = pos.addVector(distance * f1, 0, distance * f2);
        this.hand.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public void face(String direction) {
        EnumFacing turned = EnumFacing.byName(direction);
        if (turned != null) {
            this.hand.setYaw(turned.getHorizontalAngle());
        }
    }

    public void left() {
        this.hand.moveYaw(-90);
    }
    public void right() {
        this.hand.moveYaw(90);
    }
    public void reverse() {
        this.hand.moveYaw(180);
    }

    public void up() {this.up(1); }
    public void up(float distance) {
        this.hand.moveEntity(0, distance, 0);
    }

    public void down() {this.down(1); }
    public void down(float distance) {
        this.hand.moveEntity(0, -distance, 0);
    }

    public void move(int x, int y, int z) {
        this.hand.moveEntity(x, y, z);
    }

    // this is just a little crazypants
    private String[] s(String ... strings) {
        return strings;
    }

    public void put(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("put", s("blockname"), PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        IBlockState state = PyRegistry.getBlockVariant(r, this.hand.getFacedPos(), this.hand.getHorizontalFacing(),
                (WorldServer)this.world);
        this.put(this.hand.getFacedPos(),state, this.hand.getHorizontalFacing());
    }

    public void alter(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("put", s(), PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);

        BlockPos pos = this.hand.getFacedPos();
        IBlockState state = this.world.getBlockState(pos);
        EnumFacing facing = PyRegistry.getBlockFacing(state);
        IBlockState modified = PyRegistry.modifyBlockStateFromSpec(state, r, facing);
        if (state != modified) {
            this.world.setBlockState(pos, modified);
        }
    }

//        TileEntity tileentity = world.getTileEntity(pos);
//        NBTTagCompound original = tileentity.writeToNBT(new NBTTagCompound());
//        NBTTagCompound modified = original.copy();

    public void clear() {
        if (this.world == null || this.world.isRemote) return;
        BlockPos pos = this.hand.getFacedPos();
        Block b = this.world.getBlockState(pos).getBlock();
        if (!this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public void line(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("distance", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        ShapeGen.line(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void ladder(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("height", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        ShapeGen.ladder(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void floor(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("width", "depth", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        ShapeGen.floor(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void wall(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("depth", "height", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        ShapeGen.wall(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void cube(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("width", "depth", "height", "blockname"),
                PyRegistry.BLOCK_VARIATIONS);
        r.parse(args, kws);
        ShapeGen.cube(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void circle(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("radius", "blockname"),
                // TODO PyRegistry.BLOCK_VARIATIONS
                s("color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        ShapeGen.circle(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void ellipse(PyObject[] args, String[] kws) {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("line", s("radius_x", "radius_z", "blockname"),
                // TODO PyRegistry.BLOCK_VARIATIONS
                s("color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        ShapeGen.ellipse(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }

    public void roof(PyObject[] args, String[] kws) throws BlockTypeError {
        if (this.world == null || this.world.isRemote) return;
        ArgParser r = new ArgParser("roof", s("width", "depth", "blockname"),
                // TODO PyRegistry.BLOCK_VARIATIONS
                s("style", "color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        RoofGen.roof(r, this.world, this.hand.getPosition(), this.hand.getHorizontalFacing());
    }
}
