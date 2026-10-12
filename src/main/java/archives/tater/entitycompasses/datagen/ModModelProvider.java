package archives.tater.entitycompasses.datagen;

import archives.tater.entitycompasses.EntityCompasses;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;
import static net.minecraft.data.models.model.TextureMapping.getItemTexture;
import static net.minecraft.data.models.model.TextureMapping.layer0;

public class ModModelProvider extends FabricModelProvider {

    public static final Identifier ANGLE = Identifier.withDefaultNamespace("angle"); // TODO

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        generateCompass(itemModelGenerator, EntityCompasses.ENTITY_COMPASS);
    }

    private static void generateCompass(ItemModelGenerators itemModelGenerator, Item item) {
        itemModelGenerator.generateCompassItem(item);
        createOverridesModel(
                ModelTemplates.FLAT_ITEM,
                getModelLocation(item),
                layer0(getItemTexture(item, "_16")),
                IntStream.range(0, 32)
                        .mapToObj(i -> {
                            if (i == 0)
                                return new ItemOverride(getModelLocation(item), List.of(new ItemOverride.Predicate(ANGLE, 0)));

                            var index = (i + 16) % 32;
                            var angle = i / 32f - 1 / 64f;
                            return new ItemOverride(
                                    getModelLocation(item, String.format(Locale.ROOT, "_%02d", index)),
                                    List.of(new ItemOverride.Predicate(ANGLE, angle))
                            );
                        })
                        .toList(),
                itemModelGenerator.output
        );
    }

    private static Identifier createOverridesModel(ModelTemplate template, Identifier modelId, TextureMapping mapping, List<ItemOverride> overrides, BiConsumer<Identifier, Supplier<JsonElement>> output) {
        return template.create(modelId, mapping, output, (id, modelGetter) -> {
            var json = template.createBaseTemplate(id, modelGetter);

            json.add("overrides", overrides.stream().map(override -> {
                var obj = new JsonObject();
                obj.addProperty("model", override.getModel().toString());

                obj.add("predicate", override.getPredicates().collect(
                        JsonObject::new,
                        (object, predicate) -> object.addProperty(predicate.getProperty().toString(), predicate.getValue()),
                        ModModelProvider::addAll
                ));

                return obj;
            }).collect(() -> new JsonArray(overrides.size()), JsonArray::add, JsonArray::addAll));

            return json;
        });
    }

    private static void addAll(JsonObject target, JsonObject source) {
        source.entrySet().forEach(entry -> target.add(entry.getKey(), entry.getValue()));
    }
}
