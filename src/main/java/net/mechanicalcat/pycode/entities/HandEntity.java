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

package net.mechanicalcat.pycode.entities;


import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.mechanicalcat.pycode.script.IHasPythonCode;
import net.mechanicalcat.pycode.script.MyEntityPlayer;
import net.mechanicalcat.pycode.script.PythonCode;
import net.mechanicalcat.pycode.script.HandMethods;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nullable;

public class HandEntity extends Entity implements IHasPythonCode {
    private static final DataParameter<String> CODE = EntityDataManager.<String>createKey(HandEntity.class, DataSerializers.STRING);

    private static net.minecraftforge.common.IMinecartCollisionHandler collisionHandler = null;
    public PythonCode code;

    public HandEntity(World worldIn) {
        super(worldIn);
        this.noClip = true;
    }

    public HandEntity(World worldIn, @Nullable NBTTagCompound compound, double x, double y, double z, float yaw) {
        this(worldIn);
        if (compound != null) {
            this.readEntityFromNBT(compound);
        }
        this.setPositionAndRotation(x, y, z, yaw, 0);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
    }

    protected void entityInit() {
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(0.98F, 0.7F);
        this.dataManager.register(CODE, "");
        this.initCode();
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (CODE.equals(key) && this.worldObj.isRemote) {
            FMLLog.info("notifyDataManagerChange for CODE, setting to %s", this.dataManager.get(CODE));
            this.code.setCodeString(this.dataManager.get(CODE));
        }
    }

    public void initCode() {
        this.code = new PythonCode();
        this.code.setCodeString(this.dataManager.get(CODE));
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        this.code.writeToNBT(compound);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.code.readFromNBT(compound);
        this.dataManager.set(CODE, this.code.getCode());
    }

    @Override
    public boolean getAlwaysRenderNameTag() {
        return true;
    }

    @Override
    public boolean hasCustomName() {
        return this.code.hasCode();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("[has code]");
    }

    public BlockPos getFacedPos() {
        return getPosition().offset(getHorizontalFacing());
    }

    public boolean handleItemInteraction(WorldServer world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        FMLLog.info("interact with %s", this.code.getCode());
        this.code.put("hand", new HandMethods(this, player));

        // this is only ever invoked on the server
        if (heldItem == null) {
            return false;
        }
        Item item = heldItem.getItem();
        if (item == ModItems.python_wand) {
            // TODO this is a bit yuck, but hasKey doesn't know about world/pos
            this.code.ensureCompiled(world, pos);
            if (this.code.hasKey("run")) {
                this.code.invoke(world, pos, "run", new MyEntityPlayer(player));
            }
            return true;
        } else if (item instanceof PythonBookItem || item instanceof ItemWritableBook) {
            this.code.setCodeFromBook(world, pos, heldItem);
            this.dataManager.set(CODE, this.code.getCode());
            return true;
        }
        return false;
    }

    public void moveForward(float distance) {
        Vec3d pos = this.getPositionVector();
        float f1 = -MathHelper.sin(this.rotationYaw * 0.017453292F);
        float f2 = MathHelper.cos(this.rotationYaw * 0.017453292F);
        pos = pos.addVector(distance * f1, 0, distance * f2);
        this.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public void setYaw(float angle) {
        this.rotationYaw = angle % 360;
    }

    public void moveYaw(float angle) {
        this.rotationYaw = (this.rotationYaw + angle) % 360;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
        World world = player.getEntityWorld();
        return world.isRemote || this.handleItemInteraction((WorldServer)world, player, this.getPosition(), stack);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.worldObj.isRemote && !this.isDead) {
            if (this.isEntityInvulnerable(source)) {
                return false;
            } else {
                this.setBeenAttacked();
                this.removePassengers();
                this.setDead();
                if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                    ItemStack itemstack = new ItemStack(ModItems.python_hand, 1);
                    itemstack.setStackDisplayName(this.getName());
                    if (!itemstack.hasTagCompound()) {
                        itemstack.setTagCompound(new NBTTagCompound());
                    }
                    NBTTagCompound compound = itemstack.getTagCompound();
                    if (compound == null) {
                        FMLLog.severe("Python Hand itemstack NBT missing??");
                    } else {
                        this.writeToNBT(compound);
                    }
                    this.entityDropItem(itemstack, 0.0F);
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void onUpdate() {
//        this.worldObj.theProfiler.startSection("entityBaseTick");
        if (this.posY < -64.0D) {
            this.kill();
        }

//        this.setPosition(this.posX, this.posY, this.posZ);
//        this.setRotation(this.rotationYaw, this.rotationPitch);

//        this.doBlockCollisions();
//        this.handleWaterMovement();
//        this.worldObj.theProfiler.endSection();
    }

}
