package archives.tater.entitycompasses.compat.simulated;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.minecraft.world.item.Item;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;

public class SimulatedCompat {
    private static void registerNavTarget(String path, NavigationTarget target, Item item) {
        Simulated.getRegistrate().navTarget(EntityCompasses.MOD_ID + '/' + path, () -> target, item);
    }

    public static void init() {
        registerNavTarget("entity_compass", new EntityCompassNavTarget(), EntityCompassesItems.ENTITY_COMPASS);
        registerNavTarget("entity_type_compass", new EntityTypeCompassNavTarget(), EntityCompassesItems.ENTITY_TYPE_COMPASS);
    }
}
