package net.mechanicalcat.pycode.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModCrafting {
    public static void register() {
        GameRegistry.addShapedRecipe(
            new ItemStack(ModBlocks.python_block),
                "CLC",
                "LRY",
                "CYC",
                'C', Blocks.COBBLESTONE,
                'L', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                'Y', new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                'R', Items.REDSTONE
        );
        GameRegistry.addShapedRecipe(
                new ItemStack(ModItems.python_wand),
                "  L",
                " RY",
                "S  ",
                'S', Items.STICK,
                'L', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                'Y', new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                'R', Items.REDSTONE
        );
        GameRegistry.addShapedRecipe(
                new ItemStack(ModItems.python_hand),
                " L ",
                "WRY",
                " W ",
                'W', Blocks.WOOL,
                'L', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                'Y', new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                'R', Items.REDSTONE
        );
        GameRegistry.addShapelessRecipe(
                new ItemStack(ModItems.python_book),
                ModItems.python_wand,
                Items.WRITABLE_BOOK
        );
    }
}
