package archives.tater.entitycompasses.tracker;

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

public class EntityTypeTargetTracker extends TargetTracker<HolderSet<EntityType<?>>> {
    public static final int SMALL = 4 * 16;
    public static final int MEDIUM = 16 * 16;
    public static final int LARGE = 64 * 16;

    @Override
    protected @Nullable BlockPos findTarget(ServerLevel level, HolderSet<EntityType<?>> component, Vec3 origin) {
        var entity = getClosest(level, component, origin);
        if (entity == null) return null;
        return entity.blockPosition();
    }

    public static @Nullable Entity getClosest(ServerLevel level, HolderSet<EntityType<?>> component, Vec3 origin) {
        return getClosest(level, component, origin, SMALL, MEDIUM, LARGE);
    }

    public static @Nullable Entity getClosest(ServerLevel level, HolderSet<EntityType<?>> component, Vec3 origin, int... ranges) {
        for (var range : ranges) {
            var result = getClosest(level, component, range, origin);
            if (result != null) return result;
        }
        return null;
    }

    public static @Nullable Entity getClosest(ServerLevel level, HolderSet<EntityType<?>> component, int range, Vec3 origin) {
        return getClosest(level.getEntities((Entity) null, AABB.ofSize(origin, range, range, range), entity -> entity.getType().is(component)).stream(), origin);
    }

    public static @Nullable Entity getClosest(Stream<Entity> entities, Vec3 origin) {
        return entities
                .min(comparingDouble(entity -> entity.distanceToSqr(origin)))
                .orElse(null);
    }

    @Override
    protected DataComponentType<HolderSet<EntityType<?>>> componentType() {
        return EntityCompassesComponents.ENTITY_TYPE_TARGET;
    }

    @Override
    protected void sendUpdate(ServerPlayer player, HolderSet<EntityType<?>> component, TrackerTarget target) {
        ServerPlayNetworking.send(player, new EntityTypeTargetUpdatePayload(component, target));
    }
}
