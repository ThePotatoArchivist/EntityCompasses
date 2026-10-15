package archives.tater.entitycompasses.recipe;

import archives.tater.entitycompasses.registry.EntityCompassesIngredients;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record HasComponentIngredient(DataComponentType<?> component) implements CustomIngredient {
    public static final MapCodec<HasComponentIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DataComponentType.CODEC.fieldOf("component").forGetter(HasComponentIngredient::component)
    ).apply(instance, HasComponentIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HasComponentIngredient> STREAM_CODEC = StreamCodec.composite(
            DataComponentType.STREAM_CODEC, HasComponentIngredient::component,
            HasComponentIngredient::new
    );

    @Override
    public boolean test(ItemStack stack) {
        return stack.has(component);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return EntityCompassesIngredients.HAS_COMPONENT;
    }
}
