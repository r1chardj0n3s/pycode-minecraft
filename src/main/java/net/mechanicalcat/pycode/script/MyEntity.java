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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLLog;


public class MyEntity extends MyBase {
    public Entity entity;

    public MyEntity(Entity entity) {
        this.entity = entity;
    }

    public void tp(MyBlockPos pos) {
        BlockPos p = pos.blockPos;
        this.entity.setPosition(p.getX(), p.getY(), p.getZ());
    }
    public void tp(int x, int y, int z) {
        this.entity.setPosition(x, y, z);
    }

    public void move(int x, int y, int z) {
        BlockPos pos = this.entity.getPosition();
        pos = pos.add(x, y, z);
        this.entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public void ignite() {
        this.ignite(4);
    }
    public void ignite(int seconds) {
        // ignite as if immersed in lava
        FMLLog.info("FIRE??? %s", this.entity);
        this.entity.setFire(4);
    }

    @Override
    public boolean isEntity() {
        return true;
    }

    public String toString() {
        return this.entity.toString();
    }
}
