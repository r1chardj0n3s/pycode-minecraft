package net.mechanicalcat.pycode.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ModelHand extends ModelBase {
    ModelRenderer hand;

    public ModelHand() {
        textureWidth = 64;
        textureHeight = 32;
        setTextureOffset("hand.Shape9", 0, 13);
        setTextureOffset("hand.Shape12", 0, 21);
        setTextureOffset("hand.Shape14", 28, 16);
        setTextureOffset("hand.Shape17", 25, 24);
        setTextureOffset("hand.Shape10", 43, 19);
        setTextureOffset("hand.Shape2", 20, 12);
        setTextureOffset("hand.Shape1", 31, 1);
        setTextureOffset("hand.Shape3", 20, 12);
        setTextureOffset("hand.Shape4", 0, 18);
        setTextureOffset("hand.Shape5", 10, 14);
        setTextureOffset("hand.Shape8", 15, 0);
        setTextureOffset("hand.Shape6", 0, 18);
        setTextureOffset("hand.Shape7", 0, 0);

        hand = new ModelRenderer(this, "hand");
        hand.setRotationPoint(0F, 0F, 0F);
        setRotation(hand, 0F, 0F, 0F);
//        hand.mirror = true;
        hand.addBox("Shape9", 0F, 1F, -7F, 1, 2, 1);
        hand.addBox("Shape12", -1F, 1F, 1F, 3, 4, 7);
        hand.addBox("Shape14", -1F, -3F, 2F, 3, 4, 1);
        hand.addBox("Shape17", -1F, 0F, 3F, 3, 1, 1);
        hand.addBox("Shape10", -1F, 1F, -6F, 3, 2, 7);
        hand.addBox("Shape2", 0F, -1F, 6F, 1, 1, 2);
        hand.addBox("Shape1", 0F, 0F, 0F, 1, 6, 8);
        hand.addBox("Shape3", 0F, 6F, 6F, 1, 1, 2);
        hand.addBox("Shape4", 0F, -1F, 4F, 1, 1, 1);
        hand.addBox("Shape5", 0F, -3F, 1F, 1, 3, 3);
        hand.addBox("Shape8", 0F, 0F, -6F, 1, 4, 6);
        hand.addBox("Shape6", 0F, -4F, 2F, 1, 1, 1);
        hand.addBox("Shape7", 0F, 6F, 1F, 1, 1, 4);
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        hand.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}