package net.mechanicalcat.pycode.script;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;


public class RoofGen {
    IBlockState stair, slab, fill;
    EnumFacing actualFacing;
    WorldServer world;
    boolean north_south;
    int width, depth;
    BlockPos pos;

    public RoofGen(WorldServer world, String material, EnumFacing actualFacing, int aWidth, int aDepth, BlockPos pos) throws BlockTypeError {
        this.world = world;
        this.actualFacing = actualFacing;
        this.stair = getRoofStair(material);
        this.fill = this.getRoofFiller(material);
        this.slab = getSlabBlock(material).withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
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

    private IBlockState getStairBlock(String material) throws BlockTypeError {
        if (material.equals("cobblestone")) {
            return PyRegistry.getBlock("stone_stairs").getDefaultState();
        } else {
            return PyRegistry.getBlock(material.concat("_stairs")).getDefaultState();
        }
    }

    private IBlockState getRoofStair(String material) throws BlockTypeError {
        try {
            return getStairBlock(material).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM);
        } catch (BlockTypeError e) {
            return PyRegistry.getBlock(material).getDefaultState();
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
                    IBlockState block_state = stair;
                    if (x == 0) {
                        // bottom side
                        block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.EAST);
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
                        } else if (z == width-1) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                        }
                    } else if (x == depth-1) {
                        // top side
                        block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.WEST);
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
                        } else if (z == width-1) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                        }
                    } else if (z == 0) {
                        // left side
                        block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                    } else if (z == width - 1) {
                        // right side
                        block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
                    } else if (z < width-1) {
                        block_state = fill;
                    }
                    this.world.setBlockState(pos.add(x, 0, z), block_state);
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
                    IBlockState block_state = stair;
                    if (north_south) {
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                        } else if (z == width - 1) {
                            block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.NORTH);
                        } else if (box) {
                            // only fill if box gable
                            block_state = fill;
                        } else {
                            continue;
                        }
                    } else {
                        if (x == 0) {
                            block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.EAST);
                        } else if (x == depth - 1) {
                            block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.WEST);
                        } else if (box) {
                            // only fill if box gable
                            block_state = fill;
                        } else {
                            continue;
                        }
                    }
                    this.world.setBlockState(pos.add(x, 0, z), block_state);
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
                    IBlockState block_state = stair;
                    if (north_south) {
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                        } else if (box) {
                            // only fill if box gable
                            block_state = fill;
                        } else {
                            continue;
                        }
                    } else {
                        if (x == 0) {
                            block_state = block_state.withProperty(BlockStairs.FACING, EnumFacing.EAST);
                        } else if (box) {
                            // only fill if box gable
                            block_state = fill;
                        } else {
                            continue;
                        }
                    }
                    this.world.setBlockState(pos.add(x, 0, z), block_state);
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
