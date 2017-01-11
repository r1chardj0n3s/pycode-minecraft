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

package net.mechanicalcat.pycode;

import net.mechanicalcat.pycode.events.PyCodeEventHandler;
import net.mechanicalcat.pycode.init.*;
import net.mechanicalcat.pycode.proxy.CommonProxy;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.MODNAME, acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)
public class PyCode {
    @Mod.Instance
    public static PyCode instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

//    PyCodeEventHandler events = new PyCodeEventHandler();

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        System.out.println(String.format("%s (%s) %s initialising",
                Reference.MODNAME, Reference.MODID, Reference.VERSION));

//        MinecraftForge.EVENT_BUS.register(events);

        ModBlocks.init();
        ModBlocks.register();

        ModCode.init();

        ModItems.init();
        ModItems.register();

        ModEntities.register();

        ModCrafting.register();

        proxy.preInit();

        GameRegistry.registerTileEntity(PyCodeBlockTileEntity.class, Reference.MODID + "PyCodeBlockTileEntity");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        PythonEngine.getEngine();
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}
