package net.mechanicalcat.pycode.tileentity;

import net.mechanicalcat.pycode.entities.EntityEnum;
import net.mechanicalcat.pycode.script.BlockMethods;
import net.mechanicalcat.pycode.script.IHasPythonCode;
import net.mechanicalcat.pycode.script.PythonCode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

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

    public Entity getEntity() { return null; }

    public boolean handleInteraction(World world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        this.code.put("block", new BlockMethods(this, player));
        this.code.put("entities", EntityEnum.class);
        this.isPowered = world.isBlockPowered(pos);
        this.code.put("powered", this.isPowered);
        return this.code.handleInteraction((WorldServer) world, player, pos, heldItem);
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
        this.code.put("powered", this.isPowered);
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
}
