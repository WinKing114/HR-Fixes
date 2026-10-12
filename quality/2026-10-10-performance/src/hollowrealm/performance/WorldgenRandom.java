package hollowrealm.performance;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/** Keep the original stream on its owning server thread; use the entity's stream during worldgen. */
public final class WorldgenRandom {
    private WorldgenRandom() {}

    public static RandomSource forEntity(LivingEntity entity, Level level) {
        var server = level.m_7654_();
        return server != null && !server.m_18695_() ? entity.m_217043_() : level.f_46441_;
    }
}
