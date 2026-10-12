package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.network.EntityTargetUpdatePayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public interface EntityCompassesNetworking {
    static void init() {
        PayloadTypeRegistry.playS2C().register(EntityTargetUpdatePayload.TYPE, EntityTargetUpdatePayload.CODEC);
    }
}
