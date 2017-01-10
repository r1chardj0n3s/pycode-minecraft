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

import net.minecraft.command.*;
import net.minecraft.command.server.*;
import net.minecraft.world.WorldServer;

import java.util.HashMap;
import java.util.Map;

public class MyCommands {
    public static Map<String,ICommand> COMMANDS = new HashMap<>();
    public static Class[] COMMAND_CLASSES = {
        CommandKill.class,
        CommandGameMode.class,
        CommandDifficulty.class,
        CommandDefaultGameMode.class,
        CommandKill.class,
        CommandToggleDownfall.class,
        CommandWeather.class,
        CommandXP.class,
        CommandTP.class,
        CommandTeleport.class,
        CommandGive.class,
        CommandReplaceItem.class,
        CommandStats.class,
        CommandEffect.class,
        CommandEnchant.class,
        CommandParticle.class,
        CommandEmote.class,
        CommandShowSeed.class,
        CommandHelp.class,
        CommandDebug.class,
        CommandMessage.class,
        CommandBroadcast.class,
        CommandSetSpawnpoint.class,
        CommandSetDefaultSpawnpoint.class,
        CommandGameRule.class,
        CommandClearInventory.class,
        CommandTestFor.class,
        CommandSpreadPlayers.class,
        CommandPlaySound.class,
        CommandScoreboard.class,
        CommandExecuteAt.class,
        CommandTrigger.class,
        CommandSummon.class,
        CommandFill.class,
        CommandClone.class,
        CommandCompare.class,
        CommandBlockData.class,
        CommandTestForBlock.class,
        CommandMessageRaw.class,
        CommandWorldBorder.class,
        CommandTitle.class,
        CommandEntityData.class,
        CommandStopSound.class,
        CommandTime.class,
        CommandAchievement.class,       // improved
        CommandSetBlock.class,          // improved
    };

    public static void init() {
        for (Class cl : COMMAND_CLASSES) {
            ICommand command;
            try {
                command = (ICommand)cl.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                continue;
            }
            COMMANDS.put(command.getCommandName(), command);
        }
    }

    public static CurriedCommand curry(String command, WorldServer world) {
        return new CurriedCommand(COMMANDS.get(command), world);
    }

    public static class CurriedCommand {
        ICommand command;
        WorldServer world;
        public CurriedCommand(ICommand command, WorldServer world) {
            super();
            this.command = command;
            this.world = world;
        }

        public void invoke(Object sender, String... args) throws CommandException {
            ICommandSender commandSender;
            if (sender instanceof MyEntity) {
                commandSender = ((MyEntity) sender).entity;
            } else {
                commandSender = (ICommandSender)sender;
            }
            this.command.execute(this.world.getMinecraftServer(), commandSender, args);
        }
    }
}
