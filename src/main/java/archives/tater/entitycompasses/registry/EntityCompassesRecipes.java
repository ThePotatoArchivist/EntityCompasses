package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.recipe.CraftingTransmuteRecipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface EntityCompassesRecipes {
    private static <T extends Recipe<?>> RecipeSerializer<T> register(String path, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, EntityCompasses.id(path), serializer);
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> register(String path, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return register(path, new SimpleRecipeSerializer<>(codec, streamCodec));
    }

    RecipeSerializer<CraftingTransmuteRecipe> CRAFTING_TRANSMUTE = register("crafting_transmute", CraftingTransmuteRecipe.CODEC, CraftingTransmuteRecipe.STREAM_CODEC);

    static void init() {

    }

    record SimpleRecipeSerializer<T extends Recipe<?>>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) implements RecipeSerializer<T> {}
}
