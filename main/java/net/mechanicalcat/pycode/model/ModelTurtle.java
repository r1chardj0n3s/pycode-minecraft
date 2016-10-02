package net.mechanicalcat.pycode.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTurtle extends ModelBase {

    private ModelRenderer turtle;

    public ModelTurtle() {
        this.turtle = new ModelRenderer(this, 0, 0); // int texOffX, int texOffY
        this.turtle.addBox(4, 4, 4, 16, 16, 16); // float offX, float offY, float offZ, int width, int height, int depth
        this.turtle.setRotationPoint(0, 0, 0); // float rotationPointXIn, float rotationPointYIn, float rotationPointZIn
//        this.turtle.setTextureSize(16, 16);
//        this.turtle.addChild(...);  // for all the other components
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.turtle.render(scale);
    }
}
