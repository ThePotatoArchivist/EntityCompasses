package archives.tater.entitycompasses.item;

import archives.tater.entitycompasses.registry.EntityCompassesAttachments;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import static archives.tater.entitycompasses.item.EntityCompassItem.createFilledResult;

@SuppressWarnings("UnstableApiUsage")
public class EntityTypeCompassItem extends Item {

    public EntityTypeCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (target == null || !target.canModifyEntities()) return InteractionResult.PASS;

        var result = stack.copyWithCount(1);

        if (player.isSecondaryUseActive()) {
            if (!interactionTarget.getType().is(target.entities())) return InteractionResult.FAIL;

            result.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, target.withRemovedEntity(interactionTarget.getType()));
        } else {
            if (interactionTarget.getType().is(target.entities())) return InteractionResult.FAIL;

            result.set(EntityCompassesComponents.ENTITY_TYPE_TARGET, target.withAddedEntity(interactionTarget.getType()));
        }

        player.setItemInHand(hand, createFilledResult(stack, player, result, false));
        interactionTarget.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer serverPlayer)
            serverPlayer.getAttachedOrCreate(EntityCompassesAttachments.ENTITY_TYPE_TARGET_TRACKER).addTracker(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        return target != null && target.hasEntities();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var target = stack.get(EntityCompassesComponents.ENTITY_TYPE_TARGET);
        if (target != null) target.addToTooltip(context, tooltipComponents::add, tooltipFlag);
    }
}
