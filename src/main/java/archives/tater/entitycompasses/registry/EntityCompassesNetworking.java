package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.network.EntityTargetUpdatePayload;
import archives.tater.entitycompasses.network.EntityTypeTargetUpdatePayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public interface EntityCompassesNetworking {
    static void init() {
        PayloadTypeRegistry.playS2C().register(EntityTargetUpdatePayload.TYPE, EntityTargetUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityTypeTargetUpdatePayload.TYPE, EntityTypeTargetUpdatePayload.CODEC);
    }
}
