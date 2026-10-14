package archives.tater.entitycompasses.datagen;

import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

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

        shaped(RecipeCategory.TOOLS, EntityCompassesItems.ENTITY_TYPE_COMPASS)
                .pattern(" # ")
                .pattern("#%#")
                .pattern(" # ")
                .define('#', ConventionalItemTags.IRON_INGOTS)
                .define('%', Items.CALIBRATED_SCULK_SENSOR)
                .unlockedBy(getHasName(Items.CALIBRATED_SCULK_SENSOR), has(Items.CALIBRATED_SCULK_SENSOR))
                .save(recipeOutput);

        resetDataRecipe(recipeOutput, EntityCompassesItems.ENTITY_TYPE_COMPASS);
    }
}
