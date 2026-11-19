package hollowrealm.performance.mixin;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.compat.AdventureJEIPlugin;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe;
import hollowrealm.performance.SocketingJeiExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(value = AdventureJEIPlugin.class, remap = false)
public abstract class ApotheosisJeiMixin {
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        if (!Apotheosis.enableAdventure) {
            return;
        }
        registration.getSmithingCategory().addExtension(SocketingRecipe.class, new SocketingJeiExtension());
    }
}
