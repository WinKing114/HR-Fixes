package hollowrealm.performance.mixin;

import hollowrealm.performance.ChatLineHeightGuard;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatComponent.class)
public abstract class ChatLineHeightGuardMixin {
    @Redirect(
            method = "m_240691_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;m_231551_()Ljava/lang/Object;"),
            require = 1)
    private Object hrFixes$positiveLineSpacing(OptionInstance<?> option) {
        return ChatLineHeightGuard.sanitizeLineSpacing(option.m_231551_());
    }
}
