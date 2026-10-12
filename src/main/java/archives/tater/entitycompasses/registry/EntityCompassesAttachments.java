package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.tracker.EntityTargetTracker;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public interface EntityCompassesAttachments {
    AttachmentType<EntityTargetTracker> ENTITY_TARGET_TRACKER = AttachmentRegistry.createDefaulted(EntityCompasses.id("entity_target_tracker"), EntityTargetTracker::new);

    static void init() {

    }
}
