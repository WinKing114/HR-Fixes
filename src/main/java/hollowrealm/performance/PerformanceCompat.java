package hollowrealm.performance;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("hollowrealm_performance_compat")
public final class PerformanceCompat {
    private static final Logger LOG = LogManager.getLogger("HR Fixes");

    public PerformanceCompat() {
        LOG.info("HollowRealm Fixes (HR Fixes) loaded; APPLY/SKIP reports preparation gates, MERGED reports mixin application");
    }
}
