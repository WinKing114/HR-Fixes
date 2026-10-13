package hollowrealm.performance.mixin;

import cn.leolezury.eternalstarlight.common.network.SyncAttachmentsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Forge 0.9.0 passes Context.getSender() (null on S2C) into this packet. */
@Pseudo
@Mixin(value = SyncAttachmentsPacket.class, remap = false)
public abstract class AttachmentClientMixin {
    @Inject(method = "handle(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void hollowrealm$clientPlayer(Player suppliedPlayer, CallbackInfo ci) {
        if (suppliedPlayer != null) return;
        Minecraft client = Minecraft.m_91087_();
        // A queued packet may outlive a disconnect. No local world then exists.
        if (client.m_18695_() && client.f_91074_ != null) {
            ((SyncAttachmentsPacket) (Object) this).handle(client.f_91074_);
        }
        ci.cancel();
    }
}
