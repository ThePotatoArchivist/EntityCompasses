package archives.tater.entitycompasses.recipe;

import archives.tater.entitycompasses.registry.EntityCompassesIngredients;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

public record HasComponentIngredient(Ingredient base, DataComponentType<?> component, boolean inverted) implements CustomIngredient {
    public static final MapCodec<HasComponentIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("base").forGetter(HasComponentIngredient::base),
            DataComponentType.CODEC.fieldOf("component").forGetter(HasComponentIngredient::component),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(HasComponentIngredient::inverted)
    ).apply(instance, HasComponentIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HasComponentIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, HasComponentIngredient::base,
            DataComponentType.STREAM_CODEC, HasComponentIngredient::component,
            ByteBufCodecs.BOOL, HasComponentIngredient::inverted,
            HasComponentIngredient::new
    );

    public HasComponentIngredient(Ingredient base, DataComponentType<?> component) {
        this(base, component, false);
    }

    @Override
    public boolean test(ItemStack stack) {
        return base.test(stack) && (inverted ^ stack.has(component));
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return Arrays.asList(base.getItems());
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
