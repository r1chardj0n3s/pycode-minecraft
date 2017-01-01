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

package net.mechanicalcat.pycode.proxy;

import net.mechanicalcat.pycode.events.PyCodeEventHandler;
import net.mechanicalcat.pycode.gui.GuiPythonBook;
import net.mechanicalcat.pycode.init.ModBlocks;
import net.mechanicalcat.pycode.init.ModEntities;
import net.mechanicalcat.pycode.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements CommonProxy {
    private PyCodeEventHandler handler = new PyCodeEventHandler();

    @Override
    public void preInit() {
//        MinecraftForge.EVENT_BUS.register(handler);
        // unlike the other renders, this has to be registered preInit or it just fails without error
        ModEntities.registerRenders();
    }

    @Override
    public void init() {
        ModItems.registerRenders();
        ModBlocks.registerRenders();
    }

    @Override
    public void postInit() {

    }

    @Override
    public void openBook(EntityPlayer player, ItemStack book) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiPythonBook(player, book));
    }
}
