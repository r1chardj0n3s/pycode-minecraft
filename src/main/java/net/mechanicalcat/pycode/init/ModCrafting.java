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
