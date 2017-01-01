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
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import org.python.core.Py;

import java.util.HashMap;

public class PyRegistry {
    public static Block getBlock(String blockName) throws BlockTypeError {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(blockName));
        FMLLog.info("getBlock asked for '%s', got '%s'", blockName, block.getUnlocalizedName());
        if (block.getUnlocalizedName().equals("tile.air") && !blockName.equals("air")) {
            throw new BlockTypeError(blockName);
        }
        return block;
    }

    public static IBlockState getBlockVariant(ArgParser spec, BlockPos pos, EnumFacing facing, WorldServer world) {
        String blockName = spec.getString("blockname");
        Block block;
        try {
            block = PyRegistry.getBlock(blockName);
        } catch (BlockTypeError e) {
            throw Py.TypeError("Unknown block " + blockName);
        }
        IBlockState block_state = block.getDefaultState();
        boolean facingSet = false;
        EnumFacing opposite = facing.getOpposite();
        BlockPos faced = pos.offset(facing);
        PropertyDirection direction;

        if (spec.has("color")) {
            String color = spec.getString("color");
            EnumDyeColor dye = PythonCode.COLORMAP.get(color);
            if (dye == null) {
                throw Py.TypeError(blockName + " color " + color);
            }
            PropertyEnum<EnumDyeColor> prop;
            try {
                prop = (PropertyEnum<EnumDyeColor>) block.getClass().getField("COLOR").get(block);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw Py.TypeError(blockName + " cannot be colored");
            }
            block_state = block_state.withProperty(prop, dye);
        }

        if (spec.has("facing")) {
            String s = spec.getString("facing");
            EnumFacing blockFacing;
            if (s.equals("left")) {
                blockFacing = facing.rotateYCCW();
            } else if (s.equals("right")) {
                blockFacing = facing.rotateY();
            } else if (s.equals("back")) {
                blockFacing = facing.getOpposite();
            } else {
                blockFacing = PythonCode.FACINGMAP.get(s);
            }
            if (blockFacing == null) {
                throw Py.TypeError("Invalid facing " + s);
            }
            try {
                direction = (PropertyDirection) block_state.getBlock().getClass().getField("FACING").get(block_state.getBlock());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw Py.TypeError(blockName + " does not have facing");
            }
            block_state = block_state.withProperty(direction, blockFacing);
            facingSet = true;
        }

        if (spec.has("type"))
            if (block_state.getBlock() instanceof BlockPlanks) {
                String s = spec.getString("type");
                BlockPlanks.EnumType type = PyRegistry.PLANKTYPES.get(s);
                if (s == null) throw Py.TypeError(blockName + " unknown type " + s);
                block_state = block_state.withProperty(BlockPlanks.VARIANT, type);
            } else if (block_state.getBlock() instanceof BlockStoneSlab) {
                String s = spec.getString("type");
                BlockStoneSlab.EnumType type = PyRegistry.STONETYPES.get(s);
                if (s == null) throw Py.TypeError(blockName + " unknown type " + s);
                block_state = block_state.withProperty(BlockStoneSlab.VARIANT, type);
            }

        if (spec.has("half")) {
            if (block_state.getBlock() instanceof BlockStairs) {
                String s = spec.getString("half");
                BlockStairs.EnumHalf half;
                switch (s) {
                    case "top":
                        half = BlockStairs.EnumHalf.TOP;
                        break;
                    case "bottom":
                        half = BlockStairs.EnumHalf.BOTTOM;
                        break;
                    default:
                        throw Py.TypeError(blockName + " unknown half " + s);
                }
                block_state = block_state.withProperty(BlockStairs.HALF, half);
            } else if (block_state.getBlock() instanceof BlockSlab) {
                String s = spec.getString("half");
                BlockSlab.EnumBlockHalf half;
                switch (s) {
                    case "top":
                        half = BlockSlab.EnumBlockHalf.TOP;
                        break;
                    case "bottom":
                        half = BlockSlab.EnumBlockHalf.BOTTOM;
                        break;
                    default:
                        throw Py.TypeError(blockName + " unknown half " + s);
                }
                block_state = block_state.withProperty(BlockSlab.HALF, half);
            }
        }

        if (spec.has("seamless") && block_state.getBlock() instanceof BlockStoneSlab) {
            block_state = block_state.withProperty(BlockStoneSlab.SEAMLESS, spec.getBoolean("seamless"));
        }

        if (spec.has("shape") && block_state.getBlock() instanceof BlockStairs) {
            String s = spec.getString("shape");
            BlockStairs.EnumShape shape;
            switch (s) {
                case "straight":
                    shape = BlockStairs.EnumShape.STRAIGHT;
                    break;
                case "inner_left":
                    shape = BlockStairs.EnumShape.INNER_LEFT;
                    break;
                case "inner_right":
                    shape = BlockStairs.EnumShape.INNER_RIGHT;
                    break;
                case "outer_left":
                    shape = BlockStairs.EnumShape.OUTER_LEFT;
                    break;
                case "outer_right":
                    shape = BlockStairs.EnumShape.OUTER_RIGHT;
                    break;
                default:
                    throw Py.TypeError(blockName + " unknown shape " + s);
            }
            block_state = block_state.withProperty(BlockStairs.SHAPE, shape);
        }

        // if we haven't had an explicit facing set then try to determine a good one
        if (!facingSet) {
            try {
                direction = (PropertyDirection) block_state.getBlock().getClass().getField("FACING").get(block_state.getBlock());
                if (world.isAirBlock(faced)) {
                    // check whether the next pos along (pos -> faced -> farpos) is solid (attachable)
                    BlockPos farpos = faced.add(facing.getDirectionVec());
                    if (world.isSideSolid(farpos, opposite, true)) {
                        // attach in faced pos on farpos
                        block_state = block_state.withProperty(direction, opposite);
                        FMLLog.fine("attach in faced pos=%s on farpos=%s", pos, opposite);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                FMLLog.fine("attach in current pos=%s no facing", pos);
            }
        }

        return block_state;
    }

    public static final HashMap<String, String> FILLER = new HashMap<>();
    public static final HashMap<String, BlockPlanks.EnumType> PLANKTYPES = new HashMap<>();
    public static final HashMap<String, BlockStoneSlab.EnumType> STONETYPES = new HashMap<>();
    static {
        FILLER.put("oak", "planks");
        FILLER.put("stone", "stone");
        FILLER.put("brick", "brick_block");
        FILLER.put("stone_brick", "stonebrick");
        FILLER.put("nether_brick", "nether_brick");
        FILLER.put("sandstone", "sandstone");
        FILLER.put("spruce", "planks");
        FILLER.put("birch", "planks");
        FILLER.put("jungle", "planks");
        FILLER.put("acacia", "planks");
        FILLER.put("dark_oak", "planks");
        FILLER.put("quartz", "quartz_block");
        FILLER.put("red_sandstone", "red_sandstone");
        FILLER.put("purpur", "purpur_block");
        PLANKTYPES.put("oak", BlockPlanks.EnumType.OAK);
        PLANKTYPES.put("spruce", BlockPlanks.EnumType.SPRUCE);
        PLANKTYPES.put("birch", BlockPlanks.EnumType.BIRCH);
        PLANKTYPES.put("jungle", BlockPlanks.EnumType.JUNGLE);
        PLANKTYPES.put("acacia", BlockPlanks.EnumType.ACACIA);
        PLANKTYPES.put("dark_oak", BlockPlanks.EnumType.DARK_OAK);
        STONETYPES.put("stone", BlockStoneSlab.EnumType.STONE);
        STONETYPES.put("sandstone", BlockStoneSlab.EnumType.SAND);
        STONETYPES.put("wood_old", BlockStoneSlab.EnumType.WOOD);
        STONETYPES.put("cobblestone", BlockStoneSlab.EnumType.COBBLESTONE);
        STONETYPES.put("brick", BlockStoneSlab.EnumType.BRICK);
        STONETYPES.put("stone_brick", BlockStoneSlab.EnumType.SMOOTHBRICK);
        STONETYPES.put("nether_brick", BlockStoneSlab.EnumType.NETHERBRICK);
        STONETYPES.put("quartz", BlockStoneSlab.EnumType.QUARTZ);
    }
}
