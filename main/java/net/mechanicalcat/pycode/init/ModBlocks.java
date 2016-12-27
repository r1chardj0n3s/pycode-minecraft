package net.mechanicalcat.pycode.init;

import net.mechanicalcat.pycode.blocks.PythonBlock;
import net.mechanicalcat.pycode.items.PythonBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {
    public static PythonBlock python_block;
    public static PythonBlockItem python_block_item;

    public static void init() {
        python_block = new PythonBlock();
    }

    public static void register() {
        registerBlock(python_block);
    }

    public static void registerBlock(Block block) {
        GameRegistry.register(block);
        PythonBlockItem item = new PythonBlockItem(python_block);
        item.setRegistryName(block.getRegistryName());
        GameRegistry.register(item);
    }

    public static void registerRenders() {
        registerRender(python_block);
    }

    public static void registerRender(Block block) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                Item.getItemFromBlock(block),
                0,
                new ModelResourceLocation(block.getRegistryName(), "inventory")
        );
    }
}
