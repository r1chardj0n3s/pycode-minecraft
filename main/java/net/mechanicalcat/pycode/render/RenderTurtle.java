package net.mechanicalcat.pycode.render;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.entities.TurtleEntity;
import net.mechanicalcat.pycode.model.ModelTurtle;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nonnull;

public class RenderTurtle extends Render<TurtleEntity> {
    private static ResourceLocation turtleTexture;
    private static ModelBase model = new ModelTurtle();
//    public static final Factory FACTORY = new Factory();

    public RenderTurtle(RenderManager manager) {
        super(manager);
        turtleTexture = new ResourceLocation(Reference.MODID + ":textures/entities/turtle.png");
        this.shadowSize = 0.5F;
    }

    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull TurtleEntity entity) {
        return turtleTexture;
    }

    // shamelessly stolen and stripped from RenderMinecart
    public void doRender(@Nonnull TurtleEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
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

        // TODO consider whether all this scaling is necessary
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

//    private static class Factory implements IRenderFactory<TurtleEntity> {
//        @Override
//        public Render<? super TurtleEntity> createRenderFor(RenderManager manager) {
//            System.out.println("FACTORY INVOKED");
//            return new RenderTurtle(manager);
//        }
//
//    }

}
