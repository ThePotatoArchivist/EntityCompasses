package archives.tater.entitycompasses;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.Identifier;

public class EntityCompassesClient implements ClientModInitializer {
    public static final Identifier ANGLE_MODEL_PREDICATE = EntityCompasses.id("angle");

    @Override
    public void onInitializeClient() {
        ItemProperties.register(EntityCompasses.ENTITY_COMPASS, ANGLE_MODEL_PREDICATE, new CompassItemPropertyFunction((level, stack, entity) -> {
            return null;
        }));
    }
}
