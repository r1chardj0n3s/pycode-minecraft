package net.mechanicalcat.pycode.proxy;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface CommonProxy {
    public void preInit();
    public void init();
    public void postInit();
    public void openBook(EntityPlayer player, ItemStack book);
}
