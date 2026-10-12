package archives.tater.entitycompasses.client;

import archives.tater.entitycompasses.tracker.TrackerTarget;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientTargetTracker {
    private static final Map<Object, TrackerTarget> trackers = new ConcurrentHashMap<>();

    public static void clear() {
        trackers.clear();
    }

    public static void update(Object key, TrackerTarget target) {
        if (target == TrackerTarget.NONE)
            trackers.remove(key);
        else
            trackers.put(key, target);
    }

    public static TrackerTarget get(Object key) {
        return trackers.get(key);
    }
}
