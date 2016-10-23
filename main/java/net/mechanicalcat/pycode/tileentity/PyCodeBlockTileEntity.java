package net.mechanicalcat.pycode.tileentity;

import net.mechanicalcat.pycode.script.BlockMethods;
import net.mechanicalcat.pycode.script.IHasPythonCode;
import net.mechanicalcat.pycode.script.PythonCode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import java.util.Random;


public class PyCodeBlockTileEntity extends TileEntity implements IHasPythonCode, ITickable {
    private PythonCode code;
    public boolean isPowered = false;

    public PyCodeBlockTileEntity() {
        this.initCode();
    }

    public void initCode() {
        this.code = new PythonCode();
    }

    public Entity getEntity() { return null; }

    public boolean handleInteraction(World world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        this.code.put("block", new BlockMethods(this, player));
        this.code.put("powered", this.isPowered);
        return this.code.handleInteraction((WorldServer) world, player, pos, heldItem);
    }

    public void update() {
        if (!this.hasWorldObj()) return;
        if (this.worldObj.isRemote) return;

        this.isPowered = this.worldObj.isBlockPowered(pos);
        this.code.put("powered", this.isPowered);
        if (this.code.hasKey("tick")) {
            this.code.invoke((WorldServer) this.worldObj, pos, "tick");
        }
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
