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

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Deprecated
public enum EntityEnum {
    CREEPER("creeper", EntityCreeper.class),
    ZOMBIE("zombie", EntityZombie.class),
    SKELETON("skeleton",EntitySkeleton.class);

    private Class c;
    public String name;

    EntityEnum(String name, Class c) {
        this.name = name;
        this.c = c;
    }

    @Nullable
    public static EntityEnum getByName(String name) {
        for (EntityEnum e : EntityEnum.values()) {
            if (e.name.equals(name)) {
                return e;
            }
        }
        return null;
    }

    @Nullable
    public Entity spawn(World world, BlockPos pos) {
        Constructor<?> cons;
        Entity entity;
        try {
            cons = this.c.getConstructor(World.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Failed to getBlock constructor for " + this.c + " got " + e);
            return null;
        }
        try {
            entity = (Entity)cons.newInstance(world);
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
            System.out.println("Failed to construct " + this.c + " got " + e);
            return null;
        }
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        world.spawnEntityInWorld(entity);
        return entity;
    }
}
