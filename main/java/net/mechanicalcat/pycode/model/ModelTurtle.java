package net.mechanicalcat.pycode.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelTurtle extends ModelBase {
    ModelRenderer turtle;

    public ModelTurtle() {
        textureWidth = 64;
        textureHeight = 32;
        setTextureOffset("turtle.Shape9", 0, 13);
        setTextureOffset("turtle.Shape12", 0, 21);
        setTextureOffset("turtle.Shape14", 28, 16);
        setTextureOffset("turtle.Shape17", 25, 24);
        setTextureOffset("turtle.Shape10", 43, 19);
        setTextureOffset("turtle.Shape2", 20, 12);
        setTextureOffset("turtle.Shape1", 31, 1);
        setTextureOffset("turtle.Shape3", 20, 12);
        setTextureOffset("turtle.Shape4", 0, 18);
        setTextureOffset("turtle.Shape5", 10, 14);
        setTextureOffset("turtle.Shape8", 15, 0);
        setTextureOffset("turtle.Shape6", 0, 18);
        setTextureOffset("turtle.Shape7", 0, 0);

        turtle = new ModelRenderer(this, "turtle");
        turtle.setRotationPoint(0F, 0F, 0F);
        setRotation(turtle, 0F, 0F, 0F);
//        turtle.mirror = true;
        turtle.addBox("Shape9", 0F, 1F, -7F, 1, 2, 1);
        turtle.addBox("Shape12", -1F, 1F, 1F, 3, 4, 7);
        turtle.addBox("Shape14", -1F, -3F, 2F, 3, 4, 1);
        turtle.addBox("Shape17", -1F, 0F, 3F, 3, 1, 1);
        turtle.addBox("Shape10", -1F, 1F, -6F, 3, 2, 7);
        turtle.addBox("Shape2", 0F, -1F, 6F, 1, 1, 2);
        turtle.addBox("Shape1", 0F, 0F, 0F, 1, 6, 8);
        turtle.addBox("Shape3", 0F, 6F, 6F, 1, 1, 2);
        turtle.addBox("Shape4", 0F, -1F, 4F, 1, 1, 1);
        turtle.addBox("Shape5", 0F, -3F, 1F, 1, 3, 3);
        turtle.addBox("Shape8", 0F, 0F, -6F, 1, 4, 6);
        turtle.addBox("Shape6", 0F, -4F, 2F, 1, 1, 1);
        turtle.addBox("Shape7", 0F, 6F, 1F, 1, 1, 4);
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        turtle.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}