package archives.tater.entitycompasses.mixin.client;

import archives.tater.entitycompasses.registry.EntityCompassesItems;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyReturnValue(
            method = "hasAnimatedTexture",
            at = @At("RETURN")
    )
    private static boolean entityCompass(boolean original, ItemStack stack) {
        return original || stack.is(EntityCompassesItems.ENTITY_COMPASS) || stack.is(EntityCompassesItems.ENTITY_TYPE_COMPASS);
    }
}
