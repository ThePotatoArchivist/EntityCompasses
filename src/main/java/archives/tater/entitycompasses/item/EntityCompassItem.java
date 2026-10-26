package archives.tater.entitycompasses.item;

import archives.tater.entitycompasses.EntityReference;
import archives.tater.entitycompasses.registry.EntityCompassesAttachments;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EntityCompassItem extends Item {
    public static final Component UNKNOWN_ENTITY = Component.translatable("item.entitycompass.entity_comapss.unknown");

    public EntityCompassItem(Properties properties) {
        super(properties);
    }

    /// Version of [ItemUtils#createFilledResult] that is safe for [Item#interactLivingEntity]
    public static ItemStack createFilledResult(ItemStack emptyStack, Player player, ItemStack filledStack) {
        if (player.hasInfiniteMaterials()) {
            if (!player.getInventory().contains(filledStack))
                player.getInventory().add(filledStack);

            return emptyStack;
        }

        // Must check this before consume because if #interactLivingEntity empties stack the held item is forcibly set to EMPTY afterwards
        if (emptyStack.getCount() <= 1)
            return filledStack;

        emptyStack.consume(1, player);

        if (!player.getInventory().add(filledStack))
            player.drop(filledStack, false);

        return emptyStack;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        var result = stack.copyWithCount(1);
        setEntity(result, interactionTarget);
        player.setItemInHand(hand, createFilledResult(stack, player, result));
        interactionTarget.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!player.isSecondaryUseActive()) return InteractionResultHolder.pass(stack);
        var result = stack.copyWithCount(1);
        setEntity(result, player);
        player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);
        return InteractionResultHolder.success(createFilledResult(stack, player, result));
    }

    private static void setEntity(ItemStack stack, Entity entity) {
        stack.set(EntityCompassesComponents.ENTITY_TARGET, new EntityReference(entity));
        var name = entity.getName();
        stack.set(EntityCompassesComponents.ENTITY_TARGET_NAME, name);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer serverPlayer)
            serverPlayer.getAttachedOrCreate(EntityCompassesAttachments.ENTITY_TARGET_TRACKER).addTracker(stack);

        if (level instanceof ServerLevel serverLevel) {
            var target = stack.get(EntityCompassesComponents.ENTITY_TARGET);
            if (target == null) {
                stack.remove(EntityCompassesComponents.ENTITY_TARGET_NAME);
                return;
            }

            var targetEntity = target.get(serverLevel);
            if (targetEntity == null) {
                if (!stack.has(EntityCompassesComponents.ENTITY_TARGET_NAME))
                    stack.set(EntityCompassesComponents.ENTITY_TARGET_NAME, UNKNOWN_ENTITY);
                return;
            }

            var name = targetEntity.getName();
            if (!name.equals(stack.get(EntityCompassesComponents.ENTITY_TARGET_NAME)))
                stack.set(EntityCompassesComponents.ENTITY_TARGET_NAME, name);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(EntityCompassesComponents.ENTITY_TARGET) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var name = stack.get(EntityCompassesComponents.ENTITY_TARGET_NAME);
        if (name == null) return;
        tooltipComponents.add(name.copy().withStyle(ChatFormatting.GRAY));
    }
}
