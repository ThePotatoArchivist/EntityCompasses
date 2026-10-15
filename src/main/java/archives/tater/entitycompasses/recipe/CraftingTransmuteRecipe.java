package archives.tater.entitycompasses.recipe;

import archives.tater.entitycompasses.registry.EntityCompassesRecipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

public class CraftingTransmuteRecipe extends ShapelessRecipe {
    private final ItemStack result;
    private final Ingredient base;
    private final NonNullList<Ingredient> materials;

    public static final MapCodec<CraftingTransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(CraftingTransmuteRecipe::getGroup),
            CraftingBookCategory.CODEC.fieldOf("category").forGetter(CraftingTransmuteRecipe::category),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(r -> r.base),
            Ingredient.CODEC_NONEMPTY.listOf().xmap(CraftingTransmuteRecipe::toNonNullList, identity()).fieldOf("materials").forGetter(r -> r.materials)
    ).apply(instance, CraftingTransmuteRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingTransmuteRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CraftingTransmuteRecipe::getGroup,
            CraftingBookCategory.STREAM_CODEC, CraftingTransmuteRecipe::category,
            ItemStack.STREAM_CODEC, r -> r.result,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).map(CraftingTransmuteRecipe::toNonNullList, identity()), r -> r.materials,
            CraftingTransmuteRecipe::new
    );

    public CraftingTransmuteRecipe(String group, CraftingBookCategory category, ItemStack result, Ingredient base, NonNullList<Ingredient> materials) {
        super(group, category, result, NonNullList.of(Ingredient.EMPTY, Stream.concat(Stream.of(base), materials.stream()).toArray(Ingredient[]::new)));
        this.result = result;
        this.base = base;
        this.materials = materials;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!super.matches(input, level)) return false;

        // Exactly one stack should match base
        boolean found = false;
        for (var stack : iterate(input)) {
            if (!found) {
                if (base.test(stack))
                    found = true;
            } else {
                if (base.test(stack))
                    return false;
            }
        }
        return found;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        var base = find(input, this.base);
        var stack = base.transmuteCopy(result.getItem(), result.getCount());
        stack.applyComponents(result.getComponentsPatch());
        return stack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EntityCompassesRecipes.CRAFTING_TRANSMUTE;
    }

    private static ItemStack find(RecipeInput input, Ingredient ingredient) {
        for (var stack : iterate(input))
            if (ingredient.test(stack))
                return stack;
        return ItemStack.EMPTY;
    }

    private static Iterable<ItemStack> iterate(RecipeInput input) {
        return () -> new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < input.size();
            }

            @Override
            public ItemStack next() {
                return input.getItem(i++);
            }
        };
    }

    private static NonNullList<Ingredient> toNonNullList(List<Ingredient> list) {
        return NonNullList.of(Ingredient.EMPTY, list.toArray(Ingredient[]::new));
    }
}
