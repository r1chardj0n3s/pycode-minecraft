package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.minecraft.block.Block;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

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

    public void setCodeString(String code) {
        this.code = code;
    }

    public void put(String key,Object val) {
        this.engine.put(key, val);
    }

    public boolean handleInteraction(WorldServer world, EntityPlayer player, BlockPos pos, ItemStack heldItem) {
        if (heldItem == null) {
            return false;
        }
        Item item = heldItem.getItem();
        if (item == ModItems.python_wand) {
            this.engine.put("world", world);
            this.engine.put("pos", pos);
            this.engine.put("player", player);
            this.engine.put("blocks", Blocks.class);
            this.engine.put("items", Items.class);
            try {
                this.engine.eval(this.code);
                world.spawnParticle(EnumParticleTypes.CRIT, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
            } catch (ScriptException e) {
                world.spawnParticle(EnumParticleTypes.SPELL, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
                System.out.println("Error running code: " + e.getMessage());
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
            // TODO have setCode actually compile the code to check its syntax
            this.code = sbStr.toString();
            System.out.println("Code set to:" + this.code);
            world.spawnParticle(EnumParticleTypes.CRIT, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, 20, 0, 0, 0, .5, new int[0]);
            return true;
        }
        return false;
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setString("code", this.code);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.code = compound.getString("code");
    }

}
