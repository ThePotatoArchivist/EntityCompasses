package archives.tater.entitycompasses.network;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.EntityReference;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

import java.util.Optional;

public record EntityTargetUpdatePayload(EntityReference component, Optional<BlockPos> target) implements CustomPacketPayload {
    public static final Type<EntityTargetUpdatePayload> TYPE = new Type<>(EntityCompasses.id("entity_target_update"));
    public static final StreamCodec<ByteBuf, EntityTargetUpdatePayload> CODEC = StreamCodec.composite(
            EntityReference.STREAM_CODEC, EntityTargetUpdatePayload::component,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), EntityTargetUpdatePayload::target,
            EntityTargetUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
