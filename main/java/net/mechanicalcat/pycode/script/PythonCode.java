package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonBookItem;
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
import net.minecraft.world.WorldServer;
import org.python.jsr223.PyScriptEngine;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class PythonCode {
    private String code = "print 'hello world'";
    private ScriptEngine engine;

    public PythonCode() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("python");
        if (engine == null) {
            System.out.println("FAILED to get Python");
        } else {
            System.out.println("Got Python");
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

    public void invoke(WorldServer world, BlockPos pos, String method) {
        // TODO Make sure there's a method called "method" in there
        try {
            this.engine.eval(method + "()");
        } catch (ScriptException e) {
            world.spawnParticle(EnumParticleTypes.SPELL, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
            System.out.println("Error running code: " + e.getMessage());
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
                System.out.println("Could not get pages from the book!?");
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
            System.out.println("Code set to:" + this.code);
            this.eval(world, player, pos);
            return true;
        }
        return false;
    }

    private boolean eval(WorldServer world, EntityPlayer player, BlockPos pos) {
        this.engine.put("world", world);
        this.engine.put("pos", pos);
        this.engine.put("player", player);
        this.engine.put("blocks", Blocks.class);
        this.engine.put("items", Items.class);
        try {
            this.engine.eval(this.code);
            world.spawnParticle(EnumParticleTypes.CRIT, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
            return true;
        } catch (ScriptException e) {
            world.spawnParticle(EnumParticleTypes.SPELL, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
            System.out.println("Error running code: " + e.getMessage());
            return false;
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setString("code", this.code);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.code = compound.getString("code");
    }

}


