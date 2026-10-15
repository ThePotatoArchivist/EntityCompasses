package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.recipe.HasComponentIngredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface EntityCompassesIngredients {
    private static <T extends CustomIngredient> CustomIngredientSerializer<T> register(Identifier id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        var serializer = new SimpleCustomIngredientSerializer<>(id, codec, streamCodec);
        CustomIngredientSerializer.register(serializer);
        return serializer;
    }

    private static <T extends CustomIngredient> CustomIngredientSerializer<T> register(String path, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return register(EntityCompasses.id(path), codec, streamCodec);
    }

    CustomIngredientSerializer<HasComponentIngredient> HAS_COMPONENT = register("has_component", HasComponentIngredient.CODEC, HasComponentIngredient.STREAM_CODEC);

    static void init() {

    }

    record SimpleCustomIngredientSerializer<T extends CustomIngredient>(Identifier id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) implements CustomIngredientSerializer<T> {
        @Override
        public Identifier getIdentifier() {
            return id;
        }

        @Override
        public MapCodec<T> getCodec(boolean allowEmpty) {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> getPacketCodec() {
            return streamCodec;
        }
    }
}
