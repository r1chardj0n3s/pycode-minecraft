package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.EntityEnum;
import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.jsr223.PyScriptEngine;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class PythonCode {
    private String code = "print 'hello world'";
    private ScriptEngine engine;
    private World world;
    private EntityPlayer player;

    public PythonCode() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("python");
        if (engine == null) {
            FMLLog.severe("FAILED to get Python");
        } else {
            FMLLog.fine("Got Python");
        }
    }

    public void check(String code) throws ScriptException {
        ((PyScriptEngine) this.engine).compile(code);
    }

    public void setCodeString(String code) {
        this.code = code;
    }

    public void put(String key,Object val) {
        this.engine.put(key, val);
    }

    public boolean hasKey(String key) {
        return this.engine.getBindings(ScriptContext.ENGINE_SCOPE).containsKey(key);
    }

    private void failz0r(WorldServer world, BlockPos pos, String fmt, Object... args) {
        world.spawnParticle(EnumParticleTypes.SPELL, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
        FMLLog.severe(fmt, args);
    }

    public void invoke(WorldServer world, BlockPos pos, String method, Entity entity) {
        // wrap entity in MyEntity!
        PyObject func = (PyObject) this.engine.get(method);
        if (func == null) {
            FMLLog.fine("No method '%s'", method);
            return;
        }
        try {
            func.__call__(Py.java2py(entity));
        } catch (NullPointerException e) {
            this.failz0r(world, pos, "Error running code: %s", e.getMessage());
        }
    }

    public void invoke(WorldServer world, BlockPos pos, String method) {
        PyObject func = (PyObject) this.engine.get(method);
        if (func == null) {
            this.failz0r(world, pos, "Unknown function '%s'", method);
            return;
        }
        try {
            func.__call__();
        } catch (NullPointerException e) {
            this.failz0r(world, pos, "Error running code: ", e.getMessage());
        }
    }

    public boolean handleInteraction(WorldServer world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        // this is only ever invoked on the server
        if (heldItem == null) {
            return false;
        }
        Item item = heldItem.getItem();
        if (item == ModItems.python_wand) {
            if (this.hasKey("run")) {
                this.invoke(world, pos, "run");
            }
            return true;
        } else if (item instanceof PythonBookItem || item instanceof ItemWritableBook) {
            NBTTagCompound bookData = heldItem.getTagCompound();
            NBTTagList pages;
            try {
                // pages are all of type TAG_String == 8
                pages = bookData.getTagList("pages", 8);
            } catch (NullPointerException e) {
                // this should not happen!
                this.failz0r(world, pos, "Could not get pages from the book!?");
                return true;
            }
            // collapse the pages into one string
            StringBuilder sbStr = new StringBuilder();
            for(int i = 0;i<pages.tagCount();i++) {
                String s = pages.getStringTagAt(i);
                if (i > 0) sbStr.append("\n");
                sbStr.append(s);
            }
            this.code = sbStr.toString();
            FMLLog.fine("Code set to: %s", this.code);
            this.eval(world, player, pos);
            return true;
        }
        return false;
    }

    private boolean eval(WorldServer world, EntityPlayer player, BlockPos pos) {
        this.world = world;
        this.player = player;
//        this.engine.put("world", world);
//        this.engine.put("player", player);
        this.engine.put("pos", new MyBlockPos(pos));

        // I am reasonably certain that I can't just shove the methods below directly
        // into the script engine namespace because I can't pass a Runnable as a
        // value to be stored in the engine namespace.
        this.engine.put("__utils__", this);

        // So.. now I copy all those methods to set up the "functions"
        try {
            String s = "";
            for (String n : functions) {
                s += String.format("%s = __utils__.%s\n", n, n);
            }
            this.engine.eval(s);
        } catch (ScriptException e) {
            this.failz0r(world, pos, "Error setting up utils: ", e.getMessage());
            return false;
        }

        // now execute the code
        try {
            this.engine.eval(this.code);
            world.spawnParticle(EnumParticleTypes.CRIT, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
            return true;
        } catch (ScriptException e) {
            this.failz0r(world, pos, "Error running code: ", e.getMessage());
            return false;
        }
    }

    private String[] functions = {"chat", "water", "lava", "clear"};

    public void chat(String message) {
        this.player.addChatComponentMessage(new TextComponentString(message));
    }

    // MyBlockPos for python code, other one for "internal" use without shenanigans
    public void water(MyBlockPos pos) {
        this.water(pos.blockPos);
    }
    public void water(BlockPos pos) {
        if (this.world.isRemote) return;

        Block b = this.world.getBlockState(pos).getBlock();

        if (this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
        }
    }

    public void lava(MyBlockPos pos) {
        this.lava(pos.blockPos);
    }
    public void lava(BlockPos pos) {
        if (this.world.isRemote) return;

        Block b = this.world.getBlockState(pos).getBlock();

        if (this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.FLOWING_LAVA.getDefaultState());
        }
    }

    public void clear(MyBlockPos pos) {
        this.clear(pos.blockPos);
    }
    public void clear(BlockPos pos) {
        if (this.world.isRemote) return;

        Block b = this.world.getBlockState(pos).getBlock();

        if (!this.world.isAirBlock(pos)) {
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setString("code", this.code);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.code = compound.getString("code");
    }

}


