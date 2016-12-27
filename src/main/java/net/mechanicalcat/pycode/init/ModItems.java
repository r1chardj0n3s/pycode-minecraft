package net.mechanicalcat.pycode.init;

import net.mechanicalcat.pycode.items.PythonBookItem;
import net.mechanicalcat.pycode.items.HandItem;
import net.mechanicalcat.pycode.items.PythonWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    public static Item python_wand;
    public static Item python_hand;
    public static Item python_book;

    public static void init() {
        python_wand = new PythonWandItem();
        python_hand = new HandItem();
        python_book = new PythonBookItem();
    }

    public static void register() {
        GameRegistry.register(python_wand);
        GameRegistry.register(python_hand);
        GameRegistry.register(python_book);
    }

    public static void registerRenders() {
        registerRender(python_wand);
        registerRender(python_hand);
        registerRender(python_book);
    }

    public static void registerRender(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                item,
                0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")
        );
    }
}
