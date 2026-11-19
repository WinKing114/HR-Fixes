package hollowrealm.performance.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.attributeslib.client.AttributesLibClient", priority = 500, remap = false)
public abstract class LodestoneTooltipMixin {
}
