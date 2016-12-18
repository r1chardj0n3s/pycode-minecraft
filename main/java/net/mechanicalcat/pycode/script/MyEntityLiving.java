package net.mechanicalcat.pycode.script;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

public class MyEntityLiving extends MyEntity {
    public MyEntityLiving(EntityLivingBase entity) {
        super(entity);
    }

    public void potion(String effect) {
        Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(effect));

        if (potion == null) {
            FMLLog.severe("Unknown potion name '%s'", effect);
            return;
        }

        ((EntityLivingBase) this.entity).addPotionEffect(new PotionEffect(potion, 50, 1));
    }
}
