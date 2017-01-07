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

import net.mechanicalcat.pycode.script.MyEntityPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class MyEntityPlayers {
    World world;

    public MyEntityPlayers(World world) {
        this.world = world;
    }

    public MyEntityPlayer[] all() {
        List<MyEntityPlayer> l = new LinkedList<>();
        for (EntityPlayer e : world.playerEntities) {
            l.add(new MyEntityPlayer(e));
        }
        return l.toArray(new MyEntityPlayer[0]);
    }

    public MyEntityPlayer random() {
        int i = world.rand.nextInt(world.playerEntities.size());
        return new MyEntityPlayer(world.playerEntities.get(i));
    }

    @Nullable
    public MyEntityPlayer closest(BlockPos pos) {
        EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
        if (player == null) return null;
        return new MyEntityPlayer(player);
    }

    public MyEntityPlayer get(MinecraftServer server, ICommandSender sender, String target) throws PlayerNotFoundException {
        return new MyEntityPlayer(CommandBase.getPlayer(server, sender, target));
    }
}
