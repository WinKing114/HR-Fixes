package hollowrealm.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChatLineHeightGuardTest {
    private static int vanillaLineHeight(double spacing) {
        return (int) (9.0D * (spacing + 1.0D));
    }

    @Test
    void replacesAllDivideByZeroAndNonFiniteSpacingsWithZero() {
        double[] dangerous = {
                -1.0D,
                -0.95D,
                -0.90D,
                Math.nextDown(-8.0D / 9.0D),
                -2.0D,
                Double.NaN,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY
        };
        for (double spacing : dangerous) {
            Object sanitized = ChatLineHeightGuard.sanitizeLineSpacing(spacing);
            assertEquals(0.0D, sanitized, "Expected 0.0 for spacing=" + spacing);
            int lineHeight = vanillaLineHeight((Double) sanitized);
            assertTrue(lineHeight >= 1, "Line height must stay >= 1 for spacing=" + spacing);
        }
    }

    @Test
    void preservesValidSpacingsAndNonDoubleObjects() {
        double[] valid = {-8.0D / 9.0D, -0.88D, -0.5D, 0.0D, 0.25D, 0.5D, 1.0D, 2.0D};
        for (double spacing : valid) {
            Object sanitized = ChatLineHeightGuard.sanitizeLineSpacing(spacing);
            assertEquals(spacing, sanitized, "Valid spacing should remain unchanged: " + spacing);
            assertTrue(vanillaLineHeight((Double) sanitized) >= 1);
        }
        Object sentinel = new Object();
        assertEquals(sentinel, ChatLineHeightGuard.sanitizeLineSpacing(sentinel));
        assertEquals(null, ChatLineHeightGuard.sanitizeLineSpacing(null));
    }
}
