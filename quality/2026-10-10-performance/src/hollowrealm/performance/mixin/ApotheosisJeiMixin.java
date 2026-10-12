package hollowrealm.performance.mixin;

import dev.shadowsoffire.apotheosis.adventure.compat.AdventureJEIPlugin;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe;
import hollowrealm.performance.SocketingJeiExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(value = AdventureJEIPlugin.class, remap = false)
public abstract class ApotheosisJeiMixin {
    // Apotheosis 7.4.8 inherits this empty IModPlugin default. Add only its missing socketing extension.
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getSmithingCategory().addExtension(SocketingRecipe.class, new SocketingJeiExtension());
    }
}
