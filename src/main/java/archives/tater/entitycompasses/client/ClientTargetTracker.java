package archives.tater.entitycompasses.client;

import net.minecraft.core.BlockPos;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClientTargetTracker {
    private static final Map<Object, BlockPos> trackers = new ConcurrentHashMap<>();

    public static void clear() {
        trackers.clear();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static void update(Object key, Optional<BlockPos> target) {
        trackers.put(key, target.orElse(null));
    }

    public static @Nullable BlockPos get(Object key) {
        return trackers.get(key);
    }
}
