package net.mechanicalcat.pycode.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
    public Entity spawn(World world, BlockPos pos) {
        Constructor<?> cons;
        Entity entity;
        try {
            cons = this.c.getConstructor(World.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Failed to get constructor for " + this.c + " got " + e);
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
