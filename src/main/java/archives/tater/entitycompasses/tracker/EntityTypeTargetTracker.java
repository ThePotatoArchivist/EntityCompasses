package archives.tater.entitycompasses.tracker;

import archives.tater.entitycompasses.component.EntityTypeTarget;
import archives.tater.entitycompasses.network.EntityTypeTargetUpdatePayload;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static java.util.Comparator.comparingDouble;

public class EntityTypeTargetTracker extends TargetTracker<EntityTypeTarget> {
    @Override
    protected @Nullable BlockPos findTarget(ServerLevel level, EntityTypeTarget component, Vec3 origin) {
        var entity = getClosest(level, component.entities(), component.range(), origin);
        if (entity == null) return null;
        return entity.blockPosition();
    }

    public static @Nullable Entity getClosest(ServerLevel level, HolderSet<EntityType<?>> entities, int range, Vec3 origin) {
        var rangeSq = range * range;
        return getClosest(level.getEntities((Entity) null, AABB.ofSize(origin, 2 * range, 2 * range, 2 * range), entity -> entity.getType().is(entities) && entity.distanceToSqr(origin) <= rangeSq).stream(), origin);
    }

    public static @Nullable Entity getClosest(Stream<Entity> entities, Vec3 origin) {
        return entities
                .min(comparingDouble(entity -> entity.distanceToSqr(origin)))
                .orElse(null);
    }

    @Override
    protected DataComponentType<EntityTypeTarget> componentType() {
        return EntityCompassesComponents.ENTITY_TYPE_TARGET;
    }

    @Override
    protected void sendUpdate(ServerPlayer player, EntityTypeTarget component, TrackerTarget target) {
        ServerPlayNetworking.send(player, new EntityTypeTargetUpdatePayload(component, target));
    }
}
