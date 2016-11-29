package net.mechanicalcat.pycode.init;


import net.mechanicalcat.pycode.PyCode;
import net.mechanicalcat.pycode.entities.HandEntity;
import net.mechanicalcat.pycode.render.RenderHand;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
    private static int entityCount = 0;

    public static void register() {
        registerEntity(HandEntity.class, "python_hand");
    }

    private static void registerEntity(Class entity, String name) {
        // magic values from https://github.com/sky0-1/Extra-Golems-1.9/blob/master/com/golems/main/GolemEntityRegister.java#L48-L90
        EntityRegistry.registerModEntity(entity, name, ++entityCount, PyCode.instance, 48, 3, true);
    }

    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(HandEntity.class, RenderHand::new);
    }
}
