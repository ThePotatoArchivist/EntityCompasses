package archives.tater.entitycompasses.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;

import static java.util.Objects.requireNonNullElse;

public class TrackerItemPropertyFunction extends CompassItemPropertyFunction {
    public TrackerItemPropertyFunction(DataComponentType<?> componentType) {
        super((level, stack, entity) -> {
            var component = stack.get(componentType);
            if (component == null) return null;
            var target = ClientTargetTracker.get(stack.get(componentType));
            if (target == null) return null;
            return new GlobalPos(level.dimension(), target.getApproximatePosition(requireNonNullElse(Minecraft.getInstance().player, entity).position()));
        });
    }
}
