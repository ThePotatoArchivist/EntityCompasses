package archives.tater.entitycompasses.item;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.EntityReference;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityCompassItem extends Item {
    public EntityCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        stack.set(EntityCompasses.ENTITY_TARGET, new EntityReference(interactionTarget));
        player.setItemInHand(hand, stack);
        interactionTarget.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!player.isSecondaryUseActive()) return InteractionResultHolder.pass(stack);
        stack.set(EntityCompasses.ENTITY_TARGET, new EntityReference(player));
        player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(EntityCompasses.ENTITY_TARGET) || super.isFoil(stack);
    }
}
