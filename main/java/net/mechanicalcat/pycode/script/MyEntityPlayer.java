package net.mechanicalcat.pycode.script;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class MyEntityPlayer extends MyEntityLiving {
    public MyEntityPlayer(EntityPlayer player) {
        super(player);
    }

    public void chat(String message) {
        ((EntityPlayer)this.entity).addChatComponentMessage(new TextComponentString(message));
    }
}
