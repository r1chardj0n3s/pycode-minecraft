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

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;


public class RoofGen {
    private IBlockState stair, slab, fill;
    private WorldServer world;
    private boolean north_south;
    private int width, depth;
    private BlockPos pos;

    public RoofGen(WorldServer world, String material, EnumFacing actualFacing, int aWidth, int aDepth, BlockPos pos) throws BlockTypeError {
        this.world = world;
        this.stair = this.getRoofStair(material);
        if (this.stair == null) {
            this.stair = this.fill = this.slab = PyRegistry.getBlock(material).getDefaultState();
        } else {
            this.fill = this.getRoofFiller(material);
            this.slab = this.getSlabBlock(material);
        }
        this.north_south = actualFacing == EnumFacing.NORTH || actualFacing == EnumFacing.SOUTH;

        // alter pos, width and depth based on orientation so that the
        // code which always generates in the EAST facing will work
        switch (actualFacing) {
            case EAST:
                this.width = aWidth;
                this.depth = aDepth;
                this.pos = pos.add(1, 0, 0);
                break;
            case WEST:
                this.width = aWidth;
                this.depth = aDepth;
                this.pos = pos.add(-aDepth, 0, -(aWidth-1));
                break;
            case NORTH:
                this.width = aDepth;
                this.depth = aWidth;
                this.pos = pos.add(0, 0, -aDepth);
                break;
            case SOUTH:
                this.width = aDepth;
                this.depth = aWidth;
                this.pos = pos.add(-(aWidth-1), 0, 1);
                break;
        }
    }

    private IBlockState getRoofStair(String material) throws BlockTypeError {
        try {
            Block block;
            if (material.equals("cobblestone")) {
                block = PyRegistry.getBlock("stone_stairs");
            } else {
                block = PyRegistry.getBlock(material.concat("_stairs"));
            }
            return block.getDefaultState().withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM);
        } catch (BlockTypeError e) {
            return null;
        }
    }

    private IBlockState getSlabBlock(String material) throws BlockTypeError {
        Block slab;
        if (PyRegistry.PLANKTYPES.containsKey(material)) {
            slab = PyRegistry.getBlock("wooden_slab");
            return slab.getDefaultState().withProperty(BlockPlanks.VARIANT, PyRegistry.PLANKTYPES.get(material));
        } else if (PyRegistry.STONETYPES.containsKey(material)) {
            slab = PyRegistry.getBlock("stone_slab");
            return slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, PyRegistry.STONETYPES.get(material));
        } else {
            return PyRegistry.getBlock(material.concat("_slab")).getDefaultState();
        }
    }

    private IBlockState getRoofFiller(String material) throws BlockTypeError {
        String fillMaterial = PyRegistry.FILLER.get(material);
        Block filler = PyRegistry.getBlock(fillMaterial);
        filler.getDefaultState();
        if (fillMaterial.equals("planks") && !material.equals("oak")) {
            return filler.getDefaultState().withProperty(BlockPlanks.VARIANT, PyRegistry.PLANKTYPES.get(material));
        }
        return filler.getDefaultState();
    }

    public void hip() {
        while (true) {
            for (int x=0; x < depth; x++) {
                for (int z=0; z < width; z++) {
                    if (stair == fill) {
                        this.world.setBlockState(pos.add(x, 0, z), stair);
                        continue;
                    }
                    IBlockState state = stair;
                    if (x == 0) {
                        // bottom side
                        state = state.withProperty(BlockStairs.FACING, EnumFacing.EAST);
                        if (z == 0) {
                            state = state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
                        } else if (z == width-1) {
                            state = state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                        }
                    } else if (x == depth-1) {
                        // top side
                        state = state.withProperty(BlockStairs.FACING, EnumFacing.WEST);
                        if (z == 0) {
                            state = state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
                        } else if (z == width-1) {
                            state = state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                        }
                    } else if (z == 0) {
                        // left side
                        state = state.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                    } else if (z == width - 1) {
                        // right side
                        state = state.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
                    } else if (z < width-1) {
                        state = fill;
                    }
                    this.world.setBlockState(pos.add(x, 0, z), state);
                }
            }

            // move up a layer
            width -=2;
            depth -= 2;
            if (width == 1) {
                roofCap(width, depth, pos.add(1, 1, 1));
            } else if (depth == 1) {
                roofCap(width, depth, pos.add(1, 1, 1));
            }
            if (width <= 1 || depth <= 1) {
                break;
            }
            pos = pos.add(1, 1, 1);
        }
    }

    public void gable(boolean box) {
        while (true) {
            for (int x=0; x < depth; x++) {
                for (int z=0; z < width; z++) {
                    if (stair == fill) {
                        this.world.setBlockState(pos.add(x, 0, z), stair);
                        continue;
                    }
                    IBlockState state = stair;
                    if (north_south) {
                        if (z == 0) {
                            state = state.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                        } else if (z == width - 1) {
                            state = state.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
                        } else if (box) {
                            // only fill if box gable
                            state = fill;
                        } else {
                            continue;
                        }
                    } else {
                        if (x == 0) {
                            state = state.withProperty(BlockStairs.FACING, EnumFacing.EAST);
                        } else if (x == depth - 1) {
                            state = state.withProperty(BlockStairs.FACING, EnumFacing.WEST);
                        } else if (box) {
                            // only fill if box gable
                            state = fill;
                        } else {
                            continue;
                        }
                    }
                    this.world.setBlockState(pos.add(x, 0, z), state);
                }
            }

            // move up a layer, only decreasing one dimension
            if (north_south) {
                width -= 2;
                if (width == 1) {
                    roofCap(width, depth, pos.add(0, 1, 1));
                }
                if (width <= 1) {
                    break;
                }
                pos = pos.add(0, 1, 1);
            } else {
                depth -= 2;
                if (depth == 1) {
                    roofCap(width, depth, pos.add(1, 1, 0));
                }
                if (depth <= 1) {
                    break;
                }
                pos = pos.add(1, 1, 0);
            }
        }
    }

    public void shed(boolean box) {
        while (true) {
            for (int x=0; x < depth; x++) {
                for (int z=0; z < width; z++) {
                    if (stair == fill) {
                        this.world.setBlockState(pos.add(x, 0, z), stair);
                        continue;
                    }
                    IBlockState state = stair;
                    if (north_south) {
                        if (z == 0) {
                            state = state.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                        } else if (box) {
                            // only fill if box gable
                            state = fill;
                        } else {
                            continue;
                        }
                    } else {
                        if (x == 0) {
                            state = state.withProperty(BlockStairs.FACING, EnumFacing.EAST);
                        } else if (box) {
                            // only fill if box gable
                            state = fill;
                        } else {
                            continue;
                        }
                    }
                    this.world.setBlockState(pos.add(x, 0, z), state);
                }
            }

            // move up a layer, only decreasing one dimension
            if (north_south) {
                width -= 1;
                if (width < 1) {
                    break;
                }
                pos = pos.add(0, 1, 1);
            } else {
                depth -= 1;
                if (depth < 1) {
                    break;
                }
                pos = pos.add(1, 1, 0);
            }
        }
    }

    private void roofCap(int width, int depth, BlockPos pos) {
        if (width == 1) {
            for (int x = 0; x < depth; x++) {
                this.world.setBlockState(pos.add(x, 0, 0), slab);
            }
        } else if (depth == 1) {
            for (int z = 0; z < width; z++) {
                this.world.setBlockState(pos.add(0, 0, z), slab);
            }
        }
    }
}
