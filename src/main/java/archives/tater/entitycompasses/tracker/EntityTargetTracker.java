package archives.tater.entitycompasses.tracker;

import archives.tater.entitycompasses.EntityReference;
import archives.tater.entitycompasses.network.EntityTargetUpdatePayload;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class EntityTargetTracker extends TargetTracker<EntityReference> {
    @Override
    protected @Nullable BlockPos findTarget(ServerLevel level, EntityReference component, Vec3 origin) {
        var entity = component.get(level);
        if (entity == null) return null;
        return entity.blockPosition();
    }

    @Override
    protected DataComponentType<EntityReference> componentType() {
        return EntityCompassesComponents.ENTITY_TARGET;
    }

    @Override
    protected void sendUpdate(ServerPlayer player, EntityReference component, TrackerTarget target) {
        ServerPlayNetworking.send(player, new EntityTargetUpdatePayload(component, target));
    }
}
