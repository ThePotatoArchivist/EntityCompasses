package archives.tater.entitycompasses;

import archives.tater.entitycompasses.datagen.ModItemTagsProvider;
import archives.tater.entitycompasses.datagen.ModLanguageProvider;
import archives.tater.entitycompasses.datagen.ModModelProvider;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EntityCompassesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModLanguageProvider::new);
		pack.addProvider(ModItemTagsProvider::new);
	}
}
