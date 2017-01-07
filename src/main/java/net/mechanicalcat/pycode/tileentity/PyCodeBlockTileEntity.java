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

package net.mechanicalcat.pycode.tileentity;

import net.mechanicalcat.pycode.entities.EntityEnum;
import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.mechanicalcat.pycode.script.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;


public class PyCodeBlockTileEntity extends TileEntity implements IHasPythonCode, ITickable {
    private PythonCode code;
    public boolean isPowered = false;
    private int slowCountdown = -1;

    public PyCodeBlockTileEntity() {
        this.initCode();
    }

    public void initCode() {
        this.code = new PythonCode();
    }

    public PythonCode getCode() {
        return code;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.code.writeToNBT(compound);
        compound.setBoolean("isPowered", this.isPowered);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.code.readFromNBT(compound);
        this.isPowered = compound.getBoolean("isPowered");
    }

    public Entity getEntity() { return null; }

    public boolean handleItemInteraction(WorldServer world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        this.isPowered = world.isBlockPowered(pos);
        this.code.put("block", new BlockMethods(this, player));

        // this is only ever invoked on the server
        if (heldItem == null) {
            return false;
        }
        Item item = heldItem.getItem();
        if (item == ModItems.python_wand) {
            // ensure the code is compiled so we can see if run is defined
            this.code.ensureCompiled(world, pos);
            if (this.code.hasKey("run")) {
                this.code.invoke(world, pos, "run", new MyEntityPlayer(player));
            }
            return true;
        } else if (item instanceof PythonBookItem || item instanceof ItemWritableBook) {
            this.code.setCodeFromBook(world, pos, heldItem);
            return true;
        }
        return false;
    }

    public void handleEntityInteraction(MyEntity entity, String method) {
        if (!this.hasWorldObj()) return;
        if (this.worldObj.isRemote) return;
        this.code.invoke((WorldServer) this.worldObj, this.pos, method, entity);
    }

    public void update() {
        if (!this.hasWorldObj()) return;
        if (this.worldObj.isRemote) return;

        boolean isPowered = this.worldObj.isBlockPowered(pos);
        if (isPowered != this.isPowered) {
            if (isPowered) {
                if (this.code.hasKey("powerOn")) {
                    this.code.invoke((WorldServer) this.worldObj, pos, "powerOn");
                }
            } else {
                if (this.code.hasKey("powerOff")) {
                    this.code.invoke((WorldServer) this.worldObj, pos, "powerOff");
                }
            }
        }
        this.isPowered = isPowered;
        if (this.code.hasKey("tick")) {
            this.code.invoke((WorldServer) this.worldObj, pos, "tick");
        }
//
//        --this.slowCountdown;
//
//        if (!this.isOnTransferCooldown()) {
//            this.slowCountdown = 20;
//            if (this.code.hasKey("slowtick")) {
//                this.code.invoke((WorldServer) this.worldObj, pos, "slowtick");
//            }
//        }
    }
//
//    public boolean transferItem(BlockPos inPos, BlockPos outPos) {
//        EnumFacing inFacing = EnumFacing.getFacingFromVector(inPos.getX() - pos.getX(), inPos.getY() - pos.getY(), inPos.getZ() - pos.getZ());
//        EnumFacing outFacing = EnumFacing.getFacingFromVector(outPos.getX() - pos.getX(), outPos.getY() - pos.getY(), outPos.getZ() - pos.getZ());
//
//        IInventory iinventory = TileEntityHopper.getInventoryAtPosition(this.worldObj, inPos.getX(), inPos.getY(), inPos.getZ());
//        IInventory oinventory = TileEntityHopper.getInventoryAtPosition(this.worldObj, outPos.getX(), outPos.getY(), outPos.getZ());
//
//        if (iinventory == null || oinventory == null) return false;
//
//        Item item = pullItemFromInventory(iinventory, inFacing);
//        if (item == null) return false;
//
//        if (!this.isEmpty()) {
//            flag = this.transferItemsOut();
//        }
//    }
//
//    @Nullable
//    private Item  pullItemFromInventory(IInventory iinventory, EnumFacing facing) {
//        EnumFacing fromSide = facing.getOpposite();
//
//        if (isInventoryEmpty(iinventory, fromSide)) {
//            return null;
//        }
//
//        if (iinventory instanceof ISidedInventory) {
//            ISidedInventory isidedinventory = (ISidedInventory)iinventory;
//            int[] aint = isidedinventory.getSlotsForFace(fromSide);
//
//            for (int i : aint) {
//                if (TileEntityHopper.pullItemFromSlot(hopper, iinventory, i, fromSide)) {
//                    return true;
//                }
//            }
//        } else {
//            int j = iinventory.getSizeInventory();
//
//            for (int k = 0; k < j; ++k) {
//                if (TileEntityHopper.pullItemFromSlot(hopper, iinventory, k, fromSide)) {
//                    return true;
//                }
//            }
//        }
//    }

    private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory)
        {
            ISidedInventory isidedinventory = (ISidedInventory)inventoryIn;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int i : aint)
            {
                if (isidedinventory.getStackInSlot(i) != null)
                {
                    return false;
                }
            }
        }
        else
        {
            int j = inventoryIn.getSizeInventory();

            for (int k = 0; k < j; ++k)
            {
                if (inventoryIn.getStackInSlot(k) != null)
                {
                    return false;
                }
            }
        }

        return true;
    }
}
