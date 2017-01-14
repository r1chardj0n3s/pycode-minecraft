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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.python.core.PyObject;


public class ShapeGen {
    public static void line(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        if (world == null || world.isRemote) return;
        int distance = r.getInteger("distance");
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer)world);
        for (int i=0; i<distance; i++) {
            pos = pos.offset(facing);
            world.setBlockState(pos, block_state);
        }
    }

    public static void ladder(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        int height = r.getInteger("height");
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer)world);
        for (int i = 0; i < height; i++) {
            world.setBlockState(pos, block_state);
            pos = pos.add(0, 1, 0);
        }
    }

    public static void floor(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer)world);
        floor(world, r.getInteger("width"), r.getInteger("depth"), block_state, pos, facing);
    }

    public static void floor(World world, int width, int depth, IBlockState block_state, BlockPos pos, EnumFacing facing) {
        Vec3i front = facing.getDirectionVec();
        Vec3i side = facing.rotateY().getDirectionVec();
        for (int j = 0; j < width; j++) {
            BlockPos set = pos.add(side.getX() * j, 0, side.getZ() * j);
            for (int i = 0; i < depth; i++) {
                world.setBlockState(set, block_state);
                set = set.add(front);
            }
        }
    }

    public static void wall(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer) world);
        ShapeGen.wall(world, r.getInteger("depth"), r.getInteger("height"), block_state, pos, facing);
    }

    public static void wall(World world, int depth, int height, IBlockState block_state, BlockPos pos, EnumFacing facing) {
        Vec3i front = facing.getDirectionVec();
        for (int j = 0; j < height; j++) {
            BlockPos set = pos.add(0, j, 0);
            for (int i = 0; i < depth; i++) {
                world.setBlockState(set, block_state);
                set = set.add(front);
            }
        }
    }

    public static void cube(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer) world);
        ShapeGen.cube(world, r.getInteger("width"), r.getInteger("depth"), r.getInteger("height"),
                block_state, pos, facing);
    }

    public static void cube(World world, int width, int depth, int height, IBlockState block_state, BlockPos pos, EnumFacing facing) {
        floor(world, width, depth, block_state, pos, facing);
        floor(world, width, depth, block_state, pos.offset(EnumFacing.UP, height), facing);
        for (int i = 0; i < 4; i++) {
            wall(world, depth, height, block_state, pos, facing);
            pos = pos.offset(facing, depth - 1);
            facing = facing.rotateY();
        }
    }

    public static void circle(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer) world);
        ShapeGen.circle(world, r.getInteger("radius"), block_state, pos,
                r.getBoolean("fill", false));
    }

    public static void circle(World world, int radius, IBlockState block_state, BlockPos pos, boolean fill) {
        if (fill) {
            int r_squared = radius * radius;
            for (int y = -radius; y <= radius; y++) {
                int y_squared = y * y;
                for (int x = -radius; x <= radius; x++) {
                    if ((x * x) + y_squared <= r_squared) {
                        world.setBlockState(pos.add(x, 0, y), block_state);
                    }
                }
            }
        } else {
            int l = (int) (radius * Math.cos(Math.PI / 4));
            for (int x = 0; x <= l; x++) {
                int y = (int) Math.sqrt((double) (radius * radius) - (x * x));
                world.setBlockState(pos.add(+x, 0, +y), block_state);
                world.setBlockState(pos.add(+y, 0, +x), block_state);
                world.setBlockState(pos.add(-y, 0, +x), block_state);
                world.setBlockState(pos.add(-x, 0, +y), block_state);
                world.setBlockState(pos.add(-x, 0, -y), block_state);
                world.setBlockState(pos.add(-y, 0, -x), block_state);
                world.setBlockState(pos.add(+y, 0, -x), block_state);
                world.setBlockState(pos.add(+x, 0, -y), block_state);
            }
        }
    }

    public static void ellipse(ArgParser r, World world, BlockPos pos, EnumFacing facing) {
        IBlockState block_state = PyRegistry.getBlockVariant(r, pos, facing, (WorldServer) world);
        ShapeGen.ellipse(world, r.getInteger("radius_x"), r.getInteger("radius_z"), block_state,
                pos, r.getBoolean("fill", false));
    }

    public static void ellipse(World world, int radius_x, int radius_z, IBlockState block_state, BlockPos pos, boolean fill) {
        if (fill) {
            for (int x = -radius_x; x <= radius_x; x++) {
                int dy = (int) (Math.sqrt((radius_z * radius_z) * (1.0 - (double) (x * x) / (double) (radius_x * radius_x))));
                for (int y = -dy; y <= dy; y++) {
                    world.setBlockState(pos.add(x, 0, y), block_state);
                }
            }
        } else {
            double radius_x_sq = radius_x * radius_x;
            double radius_z_sq = radius_z * radius_z;
            int x = 0, y = radius_z;
            double p, px = 0, py = 2 * radius_x_sq * y;

            world.setBlockState(pos.add(+x, 0, +y), block_state);
            world.setBlockState(pos.add(-x, 0, +y), block_state);
            world.setBlockState(pos.add(+x, 0, -y), block_state);
            world.setBlockState(pos.add(-x, 0, -y), block_state);

            // Region 1
            p = radius_z_sq - (radius_x_sq * radius_z) + (0.25 * radius_x_sq);
            while (px < py) {
                x++;
                px = px + 2 * radius_z_sq;
                if (p < 0) {
                    p = p + radius_z_sq + px;
                } else {
                    y--;
                    py = py - 2 * radius_x_sq;
                    p = p + radius_z_sq + px - py;

                }
                world.setBlockState(pos.add(+x, 0, +y), block_state);
                world.setBlockState(pos.add(-x, 0, +y), block_state);
                world.setBlockState(pos.add(+x, 0, -y), block_state);
                world.setBlockState(pos.add(-x, 0, -y), block_state);
            }

            // Region 2
            p = radius_z_sq * (x + 0.5) * (x + 0.5) + radius_x_sq * (y - 1) * (y - 1) - radius_x_sq * radius_z_sq;
            while (y > 0) {
                y--;
                py = py - 2 * radius_x_sq;
                if (p > 0) {
                    p = p + radius_x_sq - py;
                } else {
                    x++;
                    px = px + 2 * radius_z_sq;
                    p = p + radius_x_sq - py + px;
                }
                world.setBlockState(pos.add(+x, 0, +y), block_state);
                world.setBlockState(pos.add(-x, 0, +y), block_state);
                world.setBlockState(pos.add(+x, 0, -y), block_state);
                world.setBlockState(pos.add(-x, 0, -y), block_state);
            }
        }
    }
}
