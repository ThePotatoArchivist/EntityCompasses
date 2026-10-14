package archives.tater.entitycompasses.component;

import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.clamp;
import static net.minecraft.util.Util.memoize;

public record EntityTypeTarget(HolderSet<EntityType<?>> entities, int range) implements TooltipProvider {
    public static final int MAX_RANGE = 512;
    public static final int MIN_RANGE = 0;
    public static final EntityTypeTarget DEFAULT = new EntityTypeTarget(HolderSet.empty(), 16);

    public static final Codec<EntityTypeTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("entities", HolderSet.empty()).forGetter(EntityTypeTarget::entities),
            Codec.intRange(MIN_RANGE, MAX_RANGE).fieldOf("range").forGetter(EntityTypeTarget::range)
    ).apply(instance, EntityTypeTarget::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityTypeTarget> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(Registries.ENTITY_TYPE), EntityTypeTarget::entities,
            ByteBufCodecs.VAR_INT, EntityTypeTarget::range,
            EntityTypeTarget::new
    );

    private static final Function<TagKey<?>, String> FALLBACK_TAG_NAME = memoize(tag -> snakeCaseToTitleCase(tag.location().getPath()));

    public boolean canModifyEntities() {
        return entities.unwrapKey().isEmpty();
    }

    public boolean hasEntities() {
        return entities.size() != 0;
    }

    public EntityTypeTarget withEntities(HolderSet<EntityType<?>> entities) {
        return new EntityTypeTarget(entities, range);
    }

    public EntityTypeTarget withAddedEntity(Holder<EntityType<?>> entity) {
        return withEntities(HolderSet.direct(Stream.concat(entities.stream(), Stream.of(entity)).toList()));
    }

    @SuppressWarnings("deprecation")
    public EntityTypeTarget withAddedEntity(EntityType<?> entity) {
        return withAddedEntity(entity.builtInRegistryHolder());
    }

    public EntityTypeTarget withRemovedEntity(Holder<EntityType<?>> entity) {
        return withEntities(HolderSet.direct(entities.stream().filter(entity2 -> entity2 != entity).toList()));
    }

    @SuppressWarnings("deprecation")
    public EntityTypeTarget withRemovedEntity(EntityType<?> entity) {
        return withRemovedEntity(entity.builtInRegistryHolder());
    }

    public EntityTypeTarget withRange(int range) {
        return new EntityTypeTarget(entities, range);
    }

    public static boolean addRange(ItemStack stack, int amount) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (target == null) return false;
        var newRange = clamp(target.range + amount, MIN_RANGE, MAX_RANGE);
        if (newRange == target.range) return false;
        stack.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, target.withRange(newRange));
        return true;
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (entities.size() != 0)
            tooltipAdder.accept(entities.unwrap().map(
                    tag -> Component.translatableWithFallback(tag.getTranslationKey(), FALLBACK_TAG_NAME.apply(tag)),
                    set -> ComponentUtils.formatList(set, type -> type.value().getDescription()).copy()
            ).withStyle(ChatFormatting.GRAY));

        tooltipAdder.accept(Component.translatable("item.entitycompasses.entity_type_compass.range", range).withStyle(ChatFormatting.GRAY));
    }

    private static String snakeCaseToTitleCase(String s) {
        return Arrays.stream(s.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

}
