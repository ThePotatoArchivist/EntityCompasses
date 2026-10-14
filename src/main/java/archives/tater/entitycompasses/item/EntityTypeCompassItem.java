package archives.tater.entitycompasses.item;

import archives.tater.entitycompasses.registry.EntityCompassesAttachments;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.util.Mth.clamp;

@SuppressWarnings("UnstableApiUsage")
public class EntityTypeCompassItem extends Item {
    public static final int MIN_RANGE = 0;
    public static final int MAX_RANGE = 512;

    public EntityTypeCompassItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        var set = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (set == null || set.unwrapKey().isPresent()) return InteractionResult.PASS;

        if (player.isSecondaryUseActive()) {
            if (!interactionTarget.getType().is(set)) return InteractionResult.FAIL;

            stack.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, HolderSet.direct(set.stream().filter(type -> type.value() != interactionTarget.getType()).toList()));
        } else {
            if (interactionTarget.getType().is(set)) return InteractionResult.FAIL;

            stack.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, HolderSet.direct(Stream.concat(set.stream(), Stream.of(interactionTarget.getType().builtInRegistryHolder())).toList()));
        }

        player.setItemInHand(hand, stack);
        interactionTarget.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer serverPlayer)
            level.getAttachedOrCreate(EntityCompassesAttachments.ENTITY_TYPE_TARGET_TRACKER).addTracker(serverPlayer, stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        return target != null && target.size() != 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (target != null && target.size() != 0)
            tooltipComponents.add(target.unwrap().map(
                    EntityTypeCompassItem::getTranslationOrFallback,
                    set -> ComponentUtils.formatList(set, type -> type.value().getDescription()).copy()
            ).withStyle(ChatFormatting.GRAY));

        var range = stack.get(EntityCompassesComponents.TARGET_RANGE);
        if (range != null) {
            tooltipComponents.add(Component.literal(range.toString()));
        }
    }

    private static MutableComponent getTranslationOrFallback(TagKey<EntityType<?>> tag) {
        var key = tag.getTranslationKey();
        if (Language.getInstance().has(key)) return Component.translatable(key);
        return Component.literal(snakeCaseToTitleCase(tag.location().getPath()));
    }

    private static String snakeCaseToTitleCase(String s) {
        return Arrays.stream(s.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static boolean addRange(ItemStack stack, int amount) {
        var range = stack.get(EntityCompassesComponents.TARGET_RANGE);
        if (range == null) return false;
        var newRange = clamp(range + amount, MIN_RANGE, MAX_RANGE);
        if (newRange == range) return false;
        stack.set(EntityCompassesComponents.TARGET_RANGE, newRange);
        return true;
    }
}
