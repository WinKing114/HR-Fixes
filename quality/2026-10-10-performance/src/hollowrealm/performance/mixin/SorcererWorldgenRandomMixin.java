package hollowrealm.performance.mixin;

import hollowrealm.performance.WorldgenRandom;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant", remap = false)
public abstract class SorcererWorldgenRandomMixin {
    @Redirect(method = "generateOffers()V", at = @At(value = "FIELD",
        target = "Lnet/minecraft/world/level/Level;f_46441_:Lnet/minecraft/util/RandomSource;",
        opcode = 180), require = 1, allow = 1, remap = false)
    private RandomSource hollowrealm$tradeRandom(Level level) {
        return WorldgenRandom.forEntity((LivingEntity) (Object) this, level);
    }
}
