package archives.tater.entitycompasses.compat.simulated;

import archives.tater.entitycompasses.registry.EntityCompassesComponents;
import archives.tater.entitycompasses.tracker.EntityTypeTargetTracker;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import org.jetbrains.annotations.Nullable;

public class EntityTypeCompassNavTarget implements NavigationTarget {
    @Override
    public @Nullable Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        if (!(navBE.getLevel() instanceof ServerLevel serverLevel)) return null;
        var target = self.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (target == null) return null;
        var entity = EntityTypeTargetTracker.getClosest(serverLevel, target, navBE.getProjectedSelfPos());
        if (entity == null) return null;
        return entity.position();
    }
}
