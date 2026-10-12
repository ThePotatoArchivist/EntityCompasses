package archives.tater.entitycompasses.item;

import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.stream.Stream;

public class EntityTypeCompassItem extends Item {
    public EntityTypeCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        var set = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (set == null || set.unwrapKey().isPresent()) return InteractionResult.PASS;

        if (player.isSecondaryUseActive()) {
            if (!interactionTarget.getType().is(set)) return InteractionResult.FAIL;

            stack.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, HolderSet.direct(set.stream().filter(type -> type.value() != interactionTarget.getType()).toList()));
        } else {
            if (interactionTarget.getType().is(set)) return InteractionResult.FAIL;

            stack.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, HolderSet.direct(Stream.concat(set.stream(), Stream.of(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(interactionTarget.getType()))).toList()));
        }

        player.setItemInHand(hand, stack);
        interactionTarget.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        return target != null && target.size() != 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (target == null || target.size() == 0) return;
        tooltipComponents.add(target.unwrap().map(
                tag -> Component.translatable(tag.getTranslationKey()),
                set -> ComponentUtils.formatList(set, type -> type.value().getDescription()).copy()
        ).withStyle(ChatFormatting.GRAY));
    }
}
