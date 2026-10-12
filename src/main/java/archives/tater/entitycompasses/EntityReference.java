package archives.tater.entitycompasses;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class EntityReference {
    private final UUID uuid;
    private @Nullable Entity entity;

    public static final Codec<EntityReference> CODEC = UUIDUtil.CODEC.xmap(EntityReference::new, EntityReference::uuid);
    public static final StreamCodec<ByteBuf, EntityReference> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(EntityReference::new, EntityReference::uuid);

    public EntityReference(UUID uuid) {
        this.uuid = uuid;
    }

    public EntityReference(Entity entity) {
        this.entity = entity;
        this.uuid = entity.getUUID();
    }

    public UUID uuid() {
        return uuid;
    }

    public @Nullable Entity get(ServerLevel level) {
        if (entity != null) {
            if (entity.isRemoved() || entity.level() != level)
                entity = null;
            else
                return entity;
        }
        return entity = level.getEntity(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EntityReference that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
