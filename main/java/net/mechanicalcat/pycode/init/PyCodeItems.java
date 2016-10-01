package net.mechanicalcat.pycode.init;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.items.PythonWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PyCodeItems {

    public static Item python_wand;

    public static void init() {
        python_wand = new PythonWandItem();
    }

    public static void register() {
        GameRegistry.register(python_wand);
    }

    public static void registerRenders() {
        registerRender(python_wand);
    }

    public static void registerRender(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                item,
                0,
                new ModelResourceLocation(item.getRegistryName(), "inventory")
        );
    }
}
