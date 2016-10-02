package net.mechanicalcat.pycode.entities;


import net.mechanicalcat.pycode.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TurtleEntity extends Entity {
    private String code = "print 'hello world'";
    private ScriptEngine engine;
    private static net.minecraftforge.common.IMinecartCollisionHandler collisionHandler = null;

    public TurtleEntity(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(0.98F, 0.7F);
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("python");
        if (engine == null) {
            System.out.println("FAILED to get Python");
        } else {
            System.out.println("Got Python");
        }
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void runCode(EntityPlayer player) throws ScriptException {
        engine.put("turtle", this);
        engine.put("blocks", Blocks.class);
        engine.put("items", Items.class);
        engine.eval(this.code);
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setString("code", this.code);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.code = compound.getString("code");
    }

    public TurtleEntity(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
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
