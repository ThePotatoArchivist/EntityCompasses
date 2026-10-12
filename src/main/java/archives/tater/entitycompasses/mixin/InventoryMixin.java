package archives.tater.entitycompasses.mixin;

import archives.tater.entitycompasses.registry.EntityCompassesAttachments;
import archives.tater.entitycompasses.tracker.TargetTracker;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void clearTrackers(CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        TargetTracker.clearTrackers(serverPlayer, EntityCompassesAttachments.ENTITY_TARGET_TRACKER);
    }
}
