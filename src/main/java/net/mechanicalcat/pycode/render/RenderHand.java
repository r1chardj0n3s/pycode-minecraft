package net.mechanicalcat.pycode.render;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.entities.HandEntity;
import net.mechanicalcat.pycode.model.ModelHand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderHand extends Render<HandEntity> {
    private static ResourceLocation handTexture;
    private static ModelBase model = new ModelHand();

    public RenderHand(RenderManager manager) {
        super(manager);
        handTexture = new ResourceLocation(Reference.MODID + ":textures/entities/hand.png");
        this.shadowSize = 0.5F;
        this.renderOutlines = true;
    }

    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull HandEntity entity) {
        return handTexture;
    }

    // shamelessly stolen and stripped from RenderMinecart
    public void doRender(@Nonnull HandEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        float f3 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.translate((float)x, (float)y + 0.7F, (float)z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-f3, 0.0F, 0.0F, 1.0F);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        model.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        } else {
            this.renderName(entity, x, y, z);
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
