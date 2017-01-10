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

package net.mechanicalcat.pycode.script;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.command.server.CommandAchievement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.text.TextComponentString;

import java.util.LinkedList;
import java.util.List;

public class MyEntityPlayer extends MyEntityLiving {
    public String name;

    public MyEntityPlayer(EntityPlayer player) {
        super(player);
        this.name = player.getName();
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public String toString() {
        return this.name;
    }

    public void chat(String message) {
        ((EntityPlayer) this.entity).addChatComponentMessage(new TextComponentString(message));
    }

    public void giveAchievement(String name) throws CommandException {
        if (!name.contains(".")) {
            name = "achievement." + name;
        }
        this.achiev("give", name);
    }

    public void takeAchievement(String name) throws CommandException {
        if (!name.contains(".")) {
            name = "achievement." + name;
        }
        this.achiev("take", name);
    }

    private void achiev(String... args) throws CommandException {
        new CommandAchievement().execute(this.entity.worldObj.getMinecraftServer(), this.entity,
               args);
    }
}
