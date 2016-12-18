package net.mechanicalcat.pycode.script;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLLog;


public class MyEntity {
    public Entity entity;

    public MyEntity(Entity entity) {
        this.entity = entity;
    }

    public void move(int x, int y, int z) {
        BlockPos pos = this.entity.getPosition();
        pos = pos.add(x, y, z);
        this.entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public String toString() {
        return this.entity.toString();
    }
}
