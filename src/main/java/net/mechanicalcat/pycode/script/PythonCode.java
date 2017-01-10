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

package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.PythonEngine;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;

import javax.annotation.Nullable;
import javax.script.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PythonCode {
    private String code = "";
    private boolean codeChanged = false;
    private SimpleScriptContext context;
    private Bindings bindings;
    private WorldServer world = null;
    private BlockPos pos;
    private ICommandSender runner;
    public static String CODE_NBT_TAG = "code";
    public MyEntityPlayers players;

    public PythonCode() {
        this.context = new SimpleScriptContext();
        this.bindings = new SimpleBindings();
        this.context.setBindings(this.bindings, ScriptContext.ENGINE_SCOPE);
    }

    public String getCode() {return code;}

    public boolean hasCode() {return !code.isEmpty();}

    public void check(String code) throws ScriptException {
        PythonEngine.compile(code);
    }

    public void setCodeString(String code) {
        this.code = code;
        this.codeChanged = true;
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setString(CODE_NBT_TAG, this.code);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.setCodeString(compound.getString(CODE_NBT_TAG));
    }

    // CODE BINDINGS
    public void put(String key,Object val) {
        this.bindings.put(key, val);
    }

    public boolean hasKey(String key) {
        return this.bindings.containsKey(key);
    }

    static public void failz0r(WorldServer world, BlockPos pos, String fmt, Object... args) {
        world.spawnParticle(EnumParticleTypes.SPELL, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
        FMLLog.severe(fmt, args);
    }

    // TODO refactor this to be more generic
    public void invoke(String method, MyEntity entity) {
        PyObject obj = (PyObject) this.bindings.get(method);
        if (obj == null) {
            failz0r(world, pos, "Unknown function '%s'", method);
            return;
        }
        PyFunction func = (PyFunction)obj;

        // handle instances of optional player argument
        PyObject co_varnames = func.__code__.__getattr__("co_varnames");
        if (entity instanceof MyEntityPlayer && !co_varnames.__contains__(Py.java2py("player"))) {
            // don't pass the player in if it's not expected
            try {
                func.__call__();
            } catch (NullPointerException e) {
                failz0r(world, pos, "Error running code: ", e.getMessage());
            }
            return;
        }

        // carry on!
        try {
            func.__call__(Py.java2py(entity));
        } catch (NullPointerException e) {
            failz0r(world, pos, "Error running code: %s", e.getMessage());
        }
    }

    public void invoke(String method, @Nullable MyBase target) {
        PyObject obj = (PyObject) this.bindings.get(method);
        if (obj == null) {
            failz0r(world, pos, "Unknown function '%s'", method);
            return;
        }
        PyFunction func = (PyFunction)obj;

        // handle instances of optional player argument
        PyObject co_varnames = func.__code__.__getattr__("co_varnames");
        if (co_varnames.__contains__(Py.java2py("target"))) {
            try {
                func.__call__(Py.java2py(target));
            } catch (NullPointerException e) {
                failz0r(world, pos, "Error running code: %s", e.getMessage());
            }
        } else {
            // don't pass the target in if it's not expected
            try {
                func.__call__();
            } catch (NullPointerException e) {
                failz0r(world, pos, "Error running code: ", e.getMessage());
            }
        }
    }

    public void invoke(String method) {
        PyObject obj = (PyObject) this.bindings.get(method);
        if (obj == null) {
            failz0r(world, pos, "Unknown function '%s'", method);
            return;
        }
        PyFunction func = (PyFunction)obj;
        try {
            func.__call__();
        } catch (NullPointerException e) {
            failz0r(world, pos, "Error running code: ", e.getMessage());
        }
    }

    public static final String bookAsString(ItemStack book) {
        NBTTagCompound bookData = book.getTagCompound();
        NBTTagList pages;
        try {
            // pages are all of type TAG_String == 8
            pages = bookData.getTagList("pages", 8);
        } catch (NullPointerException e) {
            // this should not happen!
            return null;
        }
        // collapse the pages into one string
        StringBuilder sbStr = new StringBuilder();
        for(int i = 0;i<pages.tagCount();i++) {
            String s = pages.getStringTagAt(i);
            if (i > 0) sbStr.append("\n");
            sbStr.append(s);
        }
        return sbStr.toString();
    }

    public boolean setCodeFromBook(WorldServer world, EntityPlayer player, ICommandSender runner, BlockPos pos, ItemStack heldItem) {
        String code = bookAsString(heldItem);
        if (code == null) {
            failz0r(world, pos, "Could not get pages from the book!?");
            return false;
        }
        this.setCodeString(code);
        // set context using the player so they get feedback on success/fail
        this.setContext(world, player, pos);
        // now set the default runner to be the code entity
        this.setRunner(runner);
        return true;
    }

    public void setRunner(ICommandSender runner) {
        this.runner = runner;
        this.bindings.put("__runner__", runner);
    }

    public void setContext(WorldServer world, ICommandSender runner, BlockPos pos) {
        if (this.world == world && this.runner == runner && this.pos == pos) {
            this.ensureCompiled();
            return;
        }

        this.world = world;
        this.pos = pos;
        this.runner = runner;
        this.players = new MyEntityPlayers(world);
        this.bindings.put("pos", new MyBlockPos(pos));
        if (runner instanceof EntityPlayer || runner instanceof EntityPlayerMP) {
            this.bindings.put("runner", new MyEntityPlayer((EntityPlayer)runner));
        } else if (runner instanceof Entity) {
            this.bindings.put("runner", new MyEntity((Entity) runner));
        } else if (runner instanceof TileEntity) {
            BlockPos bp = ((TileEntity) runner).getPos();
            this.bindings.put("runner", new MyBlock(world.getBlockState(bp), bp));
        }

        // I am reasonably certain that I can't just shove the methods below directly
        // into the script engine namespace because I can't pass a Runnable as a
        // value to be stored in the engine namespace.
        this.bindings.put("__utils__", this);

        // So.. now I copy all those methods to set up the "utils"
        try {
            String s = "";
            for (String n : utils) {
                s += String.format("%s = __utils__.%s\n", n, n);
            }
            PythonEngine.eval(s, this.context);
        } catch (ScriptException e) {
            failz0r(world, pos, "Error setting up utils: %s", e.getMessage());
            return;
        }

        // create the MyCommand curries and attach callables to utils / global scope
        try {
            String s = "";
            for (String n: MyCommands.COMMANDS.keySet()) {
                // bind the name to just the invoke method, using the dynamic runner value
                this.bindings.put("__" + n, MyCommands.curry(n, (WorldServer)this.world));
                s += String.format("%s = lambda *a: __%s.invoke(runner, *a)\n", n, n);
            }
            PythonEngine.eval(s, this.context);
        } catch (ScriptException e) {
            failz0r(world, pos, "Error setting up commands: %s", e.getMessage());
            return;
        }

        this.ensureCompiled();
    }

    public static final String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    private void ensureCompiled() {
        if (!this.codeChanged) return;
        FMLLog.fine("Eval my code: %s", this.code);

        // now execute the code
        try {
            PythonEngine.eval(this.code, this.context);
            world.spawnParticle(EnumParticleTypes.CRIT, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,  20, 0, 0, 0, .5, new int[0]);
        } catch (ScriptException e) {
            failz0r(world, pos, "Error running code, traceback:\n%s", stackTraceToString(e));
        }
        this.codeChanged = false;
    }

    private String[] utils = {"colors", "facings", "players"};

    public static HashMap<String, EnumDyeColor> COLORMAP = new HashMap<String, EnumDyeColor>();
    public static HashMap<String, EnumFacing> FACINGMAP = new HashMap<String, EnumFacing>();
    public static List<String> colors = new LinkedList<>();
    public static List<String> facings = new LinkedList<>();
    public static void init() {
        COLORMAP.put("white", EnumDyeColor.WHITE);
        COLORMAP.put("orange", EnumDyeColor.ORANGE);
        COLORMAP.put("magenta", EnumDyeColor.MAGENTA);
        COLORMAP.put("lightBlue", EnumDyeColor.LIGHT_BLUE);
        COLORMAP.put("yellow", EnumDyeColor.YELLOW);
        COLORMAP.put("lime", EnumDyeColor.LIME);
        COLORMAP.put("pink", EnumDyeColor.PINK);
        COLORMAP.put("gray", EnumDyeColor.GRAY);
        COLORMAP.put("silver", EnumDyeColor.SILVER);
        COLORMAP.put("cyan", EnumDyeColor.CYAN);
        COLORMAP.put("purple", EnumDyeColor.PURPLE);
        COLORMAP.put("blue", EnumDyeColor.BLUE);
        COLORMAP.put("brown", EnumDyeColor.BROWN);
        COLORMAP.put("green", EnumDyeColor.GREEN);
        COLORMAP.put("red", EnumDyeColor.RED );
        COLORMAP.put("black", EnumDyeColor.BLACK);

        FACINGMAP.put("down", EnumFacing.DOWN);
        FACINGMAP.put("up", EnumFacing.UP);
        FACINGMAP.put("north", EnumFacing.NORTH);
        FACINGMAP.put("south", EnumFacing.SOUTH);
        FACINGMAP.put("west", EnumFacing.WEST);
        FACINGMAP.put("east", EnumFacing.EAST);

        for (String name : COLORMAP.keySet()) {
            colors.add(name);
        }
        for (String name : FACINGMAP.keySet()) {
            facings.add(name);
        }
    }
}


