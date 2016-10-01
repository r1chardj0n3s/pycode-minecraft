package net.mechanicalcat.pycode.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraftforge.common.util.BlockSnapshot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class PyCodeBlockTileEntity extends TileEntity {
    private String code = "print 'hello world'";
    private ScriptEngine engine;

    public PyCodeBlockTileEntity() {
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
        // set up all the variables the code can use
        engine.put("world", this.getWorld());
        engine.put("pos", this.getPos());
        engine.put("player", player);
        engine.put("block", this);
        engine.put("blocks", Blocks.class);
        engine.put("items", Items.class);
        engine.eval(this.code);
    }

    /*
     * Convenience for setting a block
     */
    public void setBlock(Block block, BlockPos pos) {
        ((WorldServer) engine.get("world")).setBlockState(pos, block.getDefaultState());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("code", this.code);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.code = compound.getString("code");
    }
}
