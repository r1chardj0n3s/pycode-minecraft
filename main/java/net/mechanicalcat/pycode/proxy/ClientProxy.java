package net.mechanicalcat.pycode.proxy;

import net.mechanicalcat.pycode.events.PyCodeEventHandler;
import net.mechanicalcat.pycode.init.PyCodeBlocks;
import net.mechanicalcat.pycode.init.PyCodeItems;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements CommonProxy {
    private PyCodeEventHandler handler = new PyCodeEventHandler();

    @Override
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(handler);
    }

    @Override
    public void registerRenders() {
        PyCodeItems.registerRenders();
        PyCodeBlocks.registerRenders();
    }
}
