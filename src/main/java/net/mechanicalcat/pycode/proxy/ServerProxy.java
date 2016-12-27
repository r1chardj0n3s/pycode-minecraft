package net.mechanicalcat.pycode.proxy;


import net.mechanicalcat.pycode.events.PyCodeEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy implements CommonProxy {

    @Override
    public void preInit() {}

    @Override
    public void init() {}

    @Override
    public void postInit() {}

    @Override
    public void openBook(EntityPlayer player, ItemStack book) {}
}
