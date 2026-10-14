package archives.tater.entitycompasses.network;

import archives.tater.entitycompasses.EntityCompasses;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

public record RangeScrollPayload(int containerId, int stateId, int slot, int amount) implements CustomPacketPayload {

    public static final Type<RangeScrollPayload> TYPE = new Type<>(EntityCompasses.id("range_scroll"));
    public static final StreamCodec<ByteBuf, RangeScrollPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RangeScrollPayload::containerId,
            ByteBufCodecs.VAR_INT, RangeScrollPayload::stateId,
            ByteBufCodecs.VAR_INT, RangeScrollPayload::slot,
            ByteBufCodecs.VAR_INT, RangeScrollPayload::amount,
            RangeScrollPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
