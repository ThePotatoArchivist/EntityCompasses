package archives.tater.entitycompasses.network;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.component.EntityTypeTarget;
import archives.tater.entitycompasses.tracker.TrackerTarget;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EntityTypeTargetUpdatePayload(EntityTypeTarget component, TrackerTarget target) implements CustomPacketPayload {
    public static final Type<EntityTypeTargetUpdatePayload> TYPE = new Type<>(EntityCompasses.id("entity_type_target_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityTypeTargetUpdatePayload> CODEC = StreamCodec.composite(
            EntityTypeTarget.STREAM_CODEC, EntityTypeTargetUpdatePayload::component,
            TrackerTarget.CODEC, EntityTypeTargetUpdatePayload::target,
            EntityTypeTargetUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
