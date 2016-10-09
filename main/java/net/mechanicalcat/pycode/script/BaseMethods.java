package net.mechanicalcat.pycode.script;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import org.python.bouncycastle.jcajce.provider.symmetric.ARC4;

public class BaseMethods {
    protected EntityPlayer player;

    protected BaseMethods(EntityPlayer player) {
        this.player = player;
    }

    public void chat(String message) {
        this.player.addChatComponentMessage(new TextComponentString(message));
    }
}
