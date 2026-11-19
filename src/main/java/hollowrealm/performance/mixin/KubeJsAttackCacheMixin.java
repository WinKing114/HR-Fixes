package hollowrealm.performance.mixin;

import hollowrealm.performance.L2RegistrationLock;
import java.util.Collection;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "dev.xkmc.l2hostility.compat.kubejs.LHKJSPlugin", remap = false)
public abstract class KubeJsAttackCacheMixin {
    @Redirect(
            method = "clearCaches()V",
            at = @At(value = "INVOKE", target = "Ljava/util/Collection;removeIf(Ljava/util/function/Predicate;)Z"),
            require = 1,
            remap = false)
    private boolean hrFixes$removeIfUnderRegistrationLock(Collection<?> listeners, Predicate<Object> predicate) {
        Object monitor = L2RegistrationLock.monitor();
        if (monitor == null) {
            return listeners.removeIf(predicate);
        }
        synchronized (monitor) {
            return listeners.removeIf(predicate);
        }
    }
}
