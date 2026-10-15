package archives.tater.entitycompasses.datagen;

import archives.tater.entitycompasses.recipe.CraftingTransmuteRecipe;
import archives.tater.entitycompasses.recipe.HasComponentIngredient;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;
import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static void resetDataRecipe(RecipeOutput output, ItemLike item) {
        shapeless(RecipeCategory.MISC, item)
                .requires(item)
                .unlockedBy(getHasName(item), has(item))
                .save(output, getItemName(item) + "_reset");
    }

    public static void save(RecipeOutput output, Identifier id, Recipe<?> recipe, RecipeCategory category, Consumer<Advancement.Builder> requirements) {
        var builder = output.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
        requirements.accept(builder);
        output.accept(id, recipe, builder.build(id.withPrefix("recipe/" + category.getFolderName() + "/")));
    }

    public static void copyDataRecipe(RecipeOutput output, ItemLike item, DataComponentType<?> componentType) {
        save(
                output,
                output.getRecipeIdentifier(BuiltInRegistries.ITEM.getKey(item.asItem()).withSuffix("_copy")),
                new CraftingTransmuteRecipe(
                        "",
                        CraftingBookCategory.MISC,
                        new ItemStack(item.asItem(), 2),
                        DefaultCustomIngredients.all(
                                Ingredient.of(item),
                                new HasComponentIngredient(componentType).toVanilla()
                        ),
                        NonNullList.of(Ingredient.EMPTY, DefaultCustomIngredients.difference(
                                Ingredient.of(item),
                                new HasComponentIngredient(componentType).toVanilla()
                        ))
                ),
                RecipeCategory.MISC,
                builder -> builder.addCriterion(getHasName(item), has(item))
        );
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        shaped(RecipeCategory.TOOLS, EntityCompassesItems.ENTITY_COMPASS)
                .pattern(" # ")
                .pattern("#%#")
                .pattern(" # ")
                .define('#', ConventionalItemTags.IRON_INGOTS)
                .define('%', Items.ECHO_SHARD)
                .unlockedBy(getHasName(Items.ECHO_SHARD), has(Items.ECHO_SHARD))
                .save(recipeOutput);

        resetDataRecipe(recipeOutput, EntityCompassesItems.ENTITY_COMPASS);
        copyDataRecipe(recipeOutput, EntityCompassesItems.ENTITY_COMPASS, EntityCompassesComponents.ENTITY_TARGET);

        shaped(RecipeCategory.TOOLS, EntityCompassesItems.ENTITY_TYPE_COMPASS)
                .pattern(" # ")
                .pattern("#%#")
                .pattern(" # ")
                .define('#', ConventionalItemTags.IRON_INGOTS)
                .define('%', Items.CALIBRATED_SCULK_SENSOR)
                .unlockedBy(getHasName(Items.CALIBRATED_SCULK_SENSOR), has(Items.CALIBRATED_SCULK_SENSOR))
                .save(recipeOutput);

        resetDataRecipe(recipeOutput, EntityCompassesItems.ENTITY_TYPE_COMPASS);
        copyDataRecipe(recipeOutput, EntityCompassesItems.ENTITY_TYPE_COMPASS, EntityCompassesComponents.ENTITY_TYPE_TARGET);

        resetDataRecipe(recipeOutput, Items.COMPASS);
        copyDataRecipe(recipeOutput, Items.COMPASS, DataComponents.LODESTONE_TRACKER);
    }
}
