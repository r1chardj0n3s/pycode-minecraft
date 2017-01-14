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

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.server.CommandAchievement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BaseMethods {
    protected World world;

    protected BaseMethods(World world) {
        this.world = world;
    }

    // common method invoked by both hand and block
    protected void put(BlockPos pos, IBlockState block_state, EnumFacing facing) {
        // don't run on client
        if (this.world == null || this.world.isRemote) return;

        Block block = block_state.getBlock();

        FMLLog.info("Putting %s at %s", block_state, pos);

        // handle special cases
        if (block instanceof BlockDoor) {
            ItemDoor.placeDoor(this.world, pos, facing, block, true);
        } else if (block instanceof BlockBed) {
            BlockPos headpos = pos.offset(facing);
            if (this.world.getBlockState(pos.down()).isSideSolid(this.world, pos.down(), EnumFacing.UP) &&
                    this.world.getBlockState(headpos.down()).isSideSolid(this.world, headpos.down(), EnumFacing.UP)) {
                block_state = block_state
                        .withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, facing)
                        .withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
                if (this.world.setBlockState(pos, block_state, 11)) {
                    block_state = block_state.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                    this.world.setBlockState(headpos, block_state, 11);
                }
            }
        } else {
            this.world.setBlockState(pos, block_state);
        }
    }
}
