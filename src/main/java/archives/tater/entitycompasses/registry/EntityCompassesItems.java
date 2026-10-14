package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.component.EntityTypeTarget;
import archives.tater.entitycompasses.item.EntityCompassItem;
import archives.tater.entitycompasses.item.EntityTypeCompassItem;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public interface EntityCompassesItems {
    private static Item register(String path, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                EntityCompasses.id(path),
                item
        );
    }

    Item ENTITY_COMPASS = register("entity_compass", new EntityCompassItem(new Item.Properties()));

    Item ENTITY_TYPE_COMPASS = register("entity_type_compass", new EntityTypeCompassItem(new Item.Properties()
            .component(EntityCompassesComponents.ENTITY_TYPE_TARGET, EntityTypeTarget.DEFAULT)
    ));

    static void init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.addAfter(Items.COMPASS, ENTITY_COMPASS, ENTITY_TYPE_COMPASS);
        });
    }
}
