package net.mechanicalcat.pycode.script;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

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
