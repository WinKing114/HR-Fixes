package hollowrealm.performance;

public final class ChatLineHeightGuard {
    private ChatLineHeightGuard() {
    }

    public static Object sanitizeLineSpacing(Object value) {
        if (value instanceof Double spacing) {
            if (!Double.isFinite(spacing) || (int) (9.0D * (spacing + 1.0D)) < 1) {
                return 0.0D;
            }
        }
        return value;
    }
}
