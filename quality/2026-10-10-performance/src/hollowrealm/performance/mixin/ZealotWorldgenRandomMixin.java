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
@Mixin(targets = "com.qiuyue.goetyominous.common.entities.hostile.cultists.Zealot", remap = false)
public abstract class ZealotWorldgenRandomMixin {
    @Redirect(method = "populateDefaultEquipmentSlots(Lnet/minecraft/world/DifficultyInstance;)V",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/level/Level;f_46441_:Lnet/minecraft/util/RandomSource;",
            opcode = 180), require = 1, allow = 1, remap = false)
    private RandomSource hollowrealm$equipmentRandom(Level level) {
        return WorldgenRandom.forEntity((LivingEntity) (Object) this, level);
    }
}
