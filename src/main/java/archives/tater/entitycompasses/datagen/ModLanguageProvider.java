package archives.tater.entitycompasses.datagen;

import archives.tater.entitycompasses.item.EntityCompassItem;
import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(EntityCompassesItems.ENTITY_COMPASS, "Entity Compass");
        translationBuilder.add(getKey(EntityCompassItem.UNKNOWN_ENTITY), "???");
    }

    private static String getKey(Component translatable) {
        return ((TranslatableContents) translatable.getContents()).getKey();
    }
}
