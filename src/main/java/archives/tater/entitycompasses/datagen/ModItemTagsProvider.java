package archives.tater.entitycompasses.datagen;

import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("simulated", "rotate_with_nav_arrow")))
                .add(EntityCompassesItems.ENTITY_COMPASS)
                .add(EntityCompassesItems.ENTITY_TYPE_COMPASS);

    }
}
