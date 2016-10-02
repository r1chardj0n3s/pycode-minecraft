package net.mechanicalcat.pycode.init;

import net.mechanicalcat.pycode.items.TurtleItem;
import net.mechanicalcat.pycode.items.PythonWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    public static Item python_wand;
    public static Item python_turtle;

    public static void init() {
        python_wand = new PythonWandItem();
        python_turtle = new TurtleItem();
    }

    public static void register() {
        GameRegistry.register(python_wand);
        GameRegistry.register(python_turtle);
    }

    public static void registerRenders() {
        registerRender(python_wand);
        registerRender(python_turtle);
    }

    public static void registerRender(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                item,
                0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")
        );
    }
}
