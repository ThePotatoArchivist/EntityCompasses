package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.component.EntityTypeTarget;
import archives.tater.entitycompasses.network.EntityTargetUpdatePayload;
import archives.tater.entitycompasses.network.EntityTypeTargetUpdatePayload;
import archives.tater.entitycompasses.network.RangeScrollPayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface EntityCompassesNetworking {
    static void init() {
        PayloadTypeRegistry.playS2C().register(EntityTargetUpdatePayload.TYPE, EntityTargetUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityTypeTargetUpdatePayload.TYPE, EntityTypeTargetUpdatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RangeScrollPayload.TYPE, RangeScrollPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RangeScrollPayload.TYPE, (payload, context) -> {
            var player = context.player();
            var slotIndex = payload.slot();

            player.resetLastActionTime();
            AbstractContainerMenu menu = player.containerMenu;

            if (menu.containerId != payload.containerId()) return;
            if (player.isSpectator()) {
                menu.sendAllDataToRemote();
                return;
            }
            if (!menu.stillValid(player)) {
                EntityCompasses.LOGGER.debug("Player {} interacted with invalid menu {}", player, menu);
                return;
            }
            if (!menu.isValidSlotIndex(slotIndex)) {
                EntityCompasses.LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", player.getName(), slotIndex, menu.slots.size());
                return;
            }
            var stateMatch = payload.stateId() == player.containerMenu.getStateId();
            menu.suppressRemoteUpdates();
            var slot = menu.getSlot(slotIndex);
            var stack = slot.getItem();
            EntityTypeTarget.addRange(stack, payload.amount());
            menu.resumeRemoteUpdates();
            if (stateMatch) {
                menu.broadcastFullState();
            } else {
                menu.broadcastChanges();
            }
        });
    }
}
