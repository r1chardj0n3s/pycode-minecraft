package net.mechanicalcat.pycode.tileentity;

import net.mechanicalcat.pycode.script.BlockMethods;
import net.mechanicalcat.pycode.script.IHasPythonCode;
import net.mechanicalcat.pycode.script.PythonCode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;


public class PyCodeBlockTileEntity extends TileEntity implements IHasPythonCode {
    private PythonCode code;

    public PyCodeBlockTileEntity() {
        this.initCode();
    }

    public void initCode() {
        this.code = new PythonCode();
    }

    public Entity getEntity() { return null; }

    public boolean handleInteraction(World world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        this.code.put("block", new BlockMethods(this, player));
        return this.code.handleInteraction((WorldServer) world, player, pos, heldItem);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.code.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.code.readFromNBT(compound);
    }
}
