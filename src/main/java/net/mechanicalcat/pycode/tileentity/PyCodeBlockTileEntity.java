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

import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.mechanicalcat.pycode.script.*;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class PyCodeBlockTileEntity extends TileEntity implements IHasPythonCode, ITickable, ICommandSender {
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
        if (!this.worldObj.isRemote) {
            // eval and set context on loading from NBT
            this.code.setContext((WorldServer) this.worldObj, this, this.getPos());
        }
    }

    public Entity getEntity() {
        // TileEntity isn't really an entity
        return null;
    }

    public boolean handleItemInteraction(EntityPlayer player, ItemStack heldItem) {
        if (this.worldObj.isRemote) return false;
        WorldServer world = (WorldServer)this.worldObj;
        this.isPowered = world.isBlockPowered(pos);
        this.code.put("block", new BlockMethods(this, player));

        // this is only ever invoked on the server
        if (heldItem == null) {
            return false;
        }
        Item item = heldItem.getItem();
        if (item == ModItems.python_wand) {
            // set the context's ICommandSender to be this invoking player
            if (this.code.hasKey("run")) {
                this.code.setRunner(player);
                this.code.invoke("run", new MyEntityPlayer(player));
                this.code.setRunner(this);
            }
            return true;
        } else if (item instanceof PythonBookItem || item instanceof ItemWritableBook) {
            this.code.setCodeFromBook(world, player, this, pos, heldItem);
            return true;
        }
        return false;
    }

    public void handleEntityInteraction(MyEntity entity, String method) {
        if (!this.hasWorldObj()) return;
        if (this.worldObj.isRemote) return;
        this.code.invoke(method, entity);
    }

    public void update() {
        if (!this.hasWorldObj()) return;
        if (this.worldObj.isRemote) return;

        boolean isPowered = this.worldObj.isBlockPowered(pos);
        if (isPowered != this.isPowered) {
            if (isPowered) {
                if (this.code.hasKey("powerOn")) {
                    this.code.invoke("powerOn");
                }
            } else {
                if (this.code.hasKey("powerOff")) {
                    this.code.invoke("powerOff");
                }
            }
        }
        this.isPowered = isPowered;
        if (this.code.hasKey("tick")) {
            this.code.invoke("tick");
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


    // ICommandSender compatibility
    @Nullable
    public Entity getCommandSenderEntity() {
        return this.getEntity();
    }

    public World getEntityWorld() {
        return this.worldObj;
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.worldObj.getMinecraftServer();
    }

    public void addChatMessage(ITextComponent component) {
        // do nothing
    }

    public BlockPos getPosition() {
        return getPos();
    }

    public Vec3d getPositionVector() {
        BlockPos pos = getPos();
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public String getName() {
        return "[Python Block]";
    }

    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return true;
    }

    public void setCommandStat(CommandResultStats.Type type, int amount) {
        // we done store command stats like command blocks; TODO should we?
    }

    public boolean sendCommandFeedback() {
        // the block does not care :-)
        return false;
    }


    // end ICommandSender
}
