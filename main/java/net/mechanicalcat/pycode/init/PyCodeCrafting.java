package net.mechanicalcat.pycode.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PyCodeCrafting {
    public static void register() {
        GameRegistry.addShapedRecipe(
            new ItemStack(PyCodeBlocks.python_block),
                "CLC",
                "LRY",
                "CYC",
                'C', Blocks.COBBLESTONE,
                'L', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                'Y', new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                'R', Items.REDSTONE
        );
        GameRegistry.addShapedRecipe(
                new ItemStack(PyCodeItems.python_wand),
                "  L",
                " RY",
                "S  ",
                'S', Items.STICK,
                'L', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                'Y', new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                'R', Items.REDSTONE
        );
    }
}
