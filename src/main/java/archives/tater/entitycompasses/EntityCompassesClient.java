package archives.tater.entitycompasses;

import archives.tater.entitycompasses.client.ClientTargetTracker;
import archives.tater.entitycompasses.client.TrackerItemPropertyFunction;
import archives.tater.entitycompasses.network.EntityTargetUpdatePayload;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;
import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.Identifier;

public class EntityCompassesClient implements ClientModInitializer {
    public static final Identifier ANGLE_MODEL_PREDICATE = EntityCompasses.id("angle");

    @Override
    public void onInitializeClient() {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            ClientTargetTracker.clear();
        });

        ItemProperties.register(EntityCompassesItems.ENTITY_COMPASS, ANGLE_MODEL_PREDICATE, new TrackerItemPropertyFunction(EntityCompassesComponents.ENTITY_TARGET));

        ClientPlayNetworking.registerGlobalReceiver(EntityTargetUpdatePayload.TYPE, (payload, context) -> {
            ClientTargetTracker.update(payload.component(), payload.target());
        });
    }
}
