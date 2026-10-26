package archives.tater.entitycompasses.tracker;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Objects.requireNonNullElse;

@SuppressWarnings("UnstableApiUsage")
public abstract class TargetTracker<T> {
    private final Set<T> trackers = new HashSet<>();
    private final Map<T, TrackerTarget> lastPositions = new HashMap<>();

    protected abstract @Nullable BlockPos findTarget(ServerLevel level, T component, Vec3 origin);

    protected abstract DataComponentType<T> componentType();

    protected abstract void sendUpdate(ServerPlayer player, T component, TrackerTarget target);

    public void clear() {
        trackers.clear();
    }

    public void addTracker(ItemStack stack) {
        var component = stack.get(componentType());
        if (component == null) return;
        trackers.add(component);
    }

    public void tick(ServerPlayer player) {
        for (var tracker : trackers) {
            var target = TrackerTarget.create(findTarget(player.serverLevel(), tracker, player.position()), player.position());
            if (!target.equals(requireNonNullElse(lastPositions.put(tracker, target), TrackerTarget.NONE)))
                sendUpdate(player, tracker, target);
        }
    }

    public static void tick(ServerPlayer player, AttachmentType<? extends TargetTracker<?>> attachmentType) {
        var tracker = player.getAttached(attachmentType);
        if (tracker == null) return;
        tracker.tick(player);
    }

    public static void clear(ServerPlayer player, AttachmentType<? extends TargetTracker<?>> attachmentType) {
        var tracker = player.getAttached(attachmentType);
        if (tracker == null) return;
        tracker.clear();
    }
}
