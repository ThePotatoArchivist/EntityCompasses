package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.EntityReference;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EntityCompassesComponents {
    private static <T> @NotNull DataComponentType<T> register(String path, Consumer<DataComponentType.Builder<T>> init) {
        var builder = DataComponentType.<T>builder();
        init.accept(builder);
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                EntityCompasses.id(path),
                builder.build()
        );
    }

    DataComponentType<EntityReference> ENTITY_TARGET = register("entity_target", builder -> builder
            .persistent(EntityReference.CODEC)
            .networkSynchronized(EntityReference.STREAM_CODEC)
            .cacheEncoding()
    );

    DataComponentType<HolderSet<EntityType<?>>> ENTITY_TYPE_TARGET = register("entity_type_target", builder -> builder
            .persistent(RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE))
            .networkSynchronized(ByteBufCodecs.holderSet(Registries.ENTITY_TYPE))
            .cacheEncoding()
    );

    DataComponentType<Component> ENTITY_TARGET_NAME = register("entity_target_name", builder -> builder
            .persistent(ComponentSerialization.CODEC)
            .networkSynchronized(ComponentSerialization.STREAM_CODEC)
            .cacheEncoding()
    );

    DataComponentType<Integer> TARGET_RANGE = register("taget_range", builder -> builder
            .persistent(Codec.intRange(0, 512))
            .networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    static void init() {

    }
}
