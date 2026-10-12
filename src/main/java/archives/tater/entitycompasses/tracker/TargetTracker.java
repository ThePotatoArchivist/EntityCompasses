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

@SuppressWarnings("UnstableApiUsage")
public abstract class TargetTracker<T> {
    private final Map<ServerPlayer, Set<T>> trackers = new WeakHashMap<>();
    private final Map<ServerPlayer, Map<T, BlockPos>> lastPositions = new WeakHashMap<>();

    protected abstract @Nullable BlockPos findTarget(ServerLevel level, T component, Vec3 origin);

    protected abstract DataComponentType<T> componentType();

    protected abstract void sendUpdate(ServerPlayer player, T component, @Nullable BlockPos target);

    public void clearTrackers(ServerPlayer player) {
        var set = trackers.get(player);
        if (set == null) return;
        set.clear();
    }

    public void addTracker(ServerPlayer player, ItemStack stack) {
        var component = stack.get(componentType());
        if (component == null) return;
        trackers.computeIfAbsent(player, player1 -> new HashSet<>()).add(component);
    }

    public void tick(ServerLevel level) {
        trackers.forEach((serverPlayer, trackers) -> {
            var last = lastPositions.computeIfAbsent(serverPlayer, player -> new HashMap<>(trackers.size()));
            for (var tracker : trackers) {
                var target = findTarget(level, tracker, serverPlayer.position());
                if (!Objects.equals(last.put(tracker, target), target))
                    sendUpdate(serverPlayer, tracker, target);
            }
        });
    }

    public static void tick(ServerLevel level, AttachmentType<? extends TargetTracker<?>> attachmentType) {
        var tracker = level.getAttached(attachmentType);
        if (tracker == null) return;
        tracker.tick(level);
    }

    public static void clearTrackers(ServerPlayer player, AttachmentType<? extends TargetTracker<?>> attachmentType) {
        var tracker = player.serverLevel().getAttached(attachmentType);
        if (tracker == null) return;
        tracker.clearTrackers(player);
    }
}
