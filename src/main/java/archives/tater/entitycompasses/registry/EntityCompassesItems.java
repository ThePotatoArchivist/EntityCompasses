package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.item.EntityCompassItem;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

public interface EntityCompassesItems {
    private static @NotNull Item register(String path, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                EntityCompasses.id(path),
                item
        );
    }

    Item ENTITY_COMPASS = register("entity_compass", new EntityCompassItem(new Item.Properties().stacksTo(1)));

    static void init() {

    }
}
