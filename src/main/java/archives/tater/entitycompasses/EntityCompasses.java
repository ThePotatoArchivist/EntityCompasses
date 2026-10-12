package archives.tater.entitycompasses;

import archives.tater.entitycompasses.item.EntityCompassItem;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityCompasses implements ModInitializer {
    public static final String MOD_ID = "entitycompasses";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DataComponentType<EntityReference> ENTITY_TARGET = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            id("entity_target"),
            DataComponentType.<EntityReference>builder()
                    .persistent(EntityReference.CODEC)
                    .networkSynchronized(EntityReference.STREAM_CODEC)
                    .cacheEncoding()
                    .build()
    );

    public static final DataComponentType<HolderSet<EntityType<?>>> ENTITY_TYPE_TARGET = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            id("entity_type_target"),
            DataComponentType.<HolderSet<EntityType<?>>>builder()
                    .persistent(RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE))
                    .networkSynchronized(ByteBufCodecs.holderSet(Registries.ENTITY_TYPE))
                    .cacheEncoding()
                    .build()
    );

    public static final Item ENTITY_COMPASS = Registry.register(
            BuiltInRegistries.ITEM,
            id("entity_compass"),
            new EntityCompassItem(new Item.Properties().stacksTo(1))
    );

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
