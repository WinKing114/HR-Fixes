package hollowrealm.performance;

public final class L2RegistrationLock {
    private static final String OWNER = "dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler";
    private static final Object MONITOR = resolve();

    private static Object resolve() {
        try {
            return Class.forName(OWNER, false, L2RegistrationLock.class.getClassLoader());
        } catch (ClassNotFoundException | LinkageError ignored) {
            return null;
        }
    }

    public static Object monitor() {
        return MONITOR;
    }

    private L2RegistrationLock() {
    }
}
