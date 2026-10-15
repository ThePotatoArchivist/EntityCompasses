package archives.tater.entitycompasses;

import archives.tater.entitycompasses.client.ClientTargetTracker;
import archives.tater.entitycompasses.client.TrackerItemPropertyFunction;
import archives.tater.entitycompasses.component.EntityTypeTarget;
import archives.tater.entitycompasses.mixin.AbstractContainerScreenAccessor;
import archives.tater.entitycompasses.network.EntityTargetUpdatePayload;
import archives.tater.entitycompasses.network.EntityTypeTargetUpdatePayload;
import archives.tater.entitycompasses.network.RangeScrollPayload;
import archives.tater.entitycompasses.registry.EntityCompassesComponents;
import archives.tater.entitycompasses.registry.EntityCompassesItems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.Identifier;

public class EntityCompassesClient implements ClientModInitializer {
    public static final Identifier ANGLE_MODEL_PREDICATE = EntityCompasses.id("angle");
    private static double accScroll = 0;

    private static boolean onMouseScrolled(AbstractContainerScreen<?> screen, double x, double y, double scroll) {
        var slot = ((AbstractContainerScreenAccessor) screen).callFindSlot(x, y);
        if (slot == null) return true;
        var stack = slot.getItem();
        if (!stack.has(EntityCompassesComponents.ENTITY_TYPE_TARGET)) return true;

        if (accScroll * scroll < 0)
            accScroll = 0;
        accScroll += scroll;
        var amt = (int) accScroll;
        accScroll -= amt;
        if (amt == 0)
            return false;

        var changeAmount = Screen.hasShiftDown()
                ? -10 * amt
                : -amt;

        if (!EntityTypeTarget.addRange(stack, changeAmount)) return false;

        ClientPlayNetworking.send(new RangeScrollPayload(
                screen.getMenu().containerId,
                screen.getMenu().getStateId(),
                slot.index,
                changeAmount
        ));

        return false;
    }

    @Override
    public void onInitializeClient() {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            ClientTargetTracker.clear();
        });

        ItemProperties.register(EntityCompassesItems.ENTITY_COMPASS, ANGLE_MODEL_PREDICATE, new TrackerItemPropertyFunction(EntityCompassesComponents.ENTITY_TARGET));
        ItemProperties.register(EntityCompassesItems.ENTITY_TYPE_COMPASS, ANGLE_MODEL_PREDICATE, new TrackerItemPropertyFunction(EntityCompassesComponents.ENTITY_TYPE_TARGET));

        ClientPlayNetworking.registerGlobalReceiver(EntityTargetUpdatePayload.TYPE, (payload, context) -> {
            ClientTargetTracker.update(payload.component(), payload.target());
        });
        ClientPlayNetworking.registerGlobalReceiver(EntityTypeTargetUpdatePayload.TYPE, (payload, context) -> {
            ClientTargetTracker.update(payload.component(), payload.target());
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof AbstractContainerScreen<?> abstractContainerScreen) {
                ScreenMouseEvents.allowMouseScroll(screen).register((_screen, x, y, horiz, vert) ->
                        onMouseScrolled(abstractContainerScreen, x, y, vert));
            }
        });
    }
}
