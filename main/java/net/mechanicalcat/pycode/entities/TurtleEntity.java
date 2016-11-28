package net.mechanicalcat.pycode.entities;


import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.script.IHasPythonCode;
import net.mechanicalcat.pycode.script.PythonCode;
import net.mechanicalcat.pycode.script.TurtleMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

public class TurtleEntity extends Entity implements IHasPythonCode {
    private static net.minecraftforge.common.IMinecartCollisionHandler collisionHandler = null;
    private PythonCode code;
    public boolean noClip = true;

    public TurtleEntity(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(0.98F, 0.7F);
        this.initCode();
    }

    public TurtleEntity(World worldIn, double x, double y, double z, float yaw) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(0.98F, 0.7F);
        this.setPositionAndRotation(x, y, z, yaw, 0);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.initCode();
    }

    public void initCode() {
        this.code = new PythonCode();
    }

    public boolean handleInteraction(World world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        this.code.put("turtle", new TurtleMethods(this, player));
        return this.code.handleInteraction((WorldServer) world, player, pos, heldItem);
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        this.code.writeToNBT(compound);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.code.readFromNBT(compound);
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

    protected void entityInit() {
//        this.dataManager.register(ROLLING_AMPLITUDE, Integer.valueOf(0));
//        this.dataManager.register(ROLLING_DIRECTION, Integer.valueOf(1));
//        this.dataManager.register(DAMAGE, Float.valueOf(0.0F));
//        this.dataManager.register(DISPLAY_TILE, Integer.valueOf(0));
//        this.dataManager.register(DISPLAY_TILE_OFFSET, Integer.valueOf(6));
//        this.dataManager.register(SHOW_BLOCK, Boolean.valueOf(false));
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
        World world = player.getEntityWorld();
        return world.isRemote || this.handleInteraction(world, player, this.getPosition(), stack);
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
                    ItemStack itemstack = new ItemStack(ModItems.python_turtle, 1);

                    if (this.getName() != null) {
                        itemstack.setStackDisplayName(this.getName());
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
