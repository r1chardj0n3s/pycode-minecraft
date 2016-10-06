package net.mechanicalcat.pycode;

import net.mechanicalcat.pycode.init.ModBlocks;
import net.mechanicalcat.pycode.init.ModCrafting;
import net.mechanicalcat.pycode.init.ModEntities;
import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.proxy.CommonProxy;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
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

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ModBlocks.init();
        ModBlocks.register();

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
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}
