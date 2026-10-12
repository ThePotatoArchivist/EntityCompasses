package archives.tater.entitycompasses.registry;

import archives.tater.entitycompasses.EntityCompasses;
import archives.tater.entitycompasses.tracker.EntityTargetTracker;
import archives.tater.entitycompasses.tracker.EntityTypeTargetTracker;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public interface EntityCompassesAttachments {
    AttachmentType<EntityTargetTracker> ENTITY_TARGET_TRACKER = AttachmentRegistry.createDefaulted(EntityCompasses.id("entity_target_tracker"), EntityTargetTracker::new);
    AttachmentType<EntityTypeTargetTracker> ENTITY_TYPE_TARGET_TRACKER = AttachmentRegistry.createDefaulted(EntityCompasses.id("entity_type_target_tracker"), EntityTypeTargetTracker::new);

    static void init() {

    }
}
