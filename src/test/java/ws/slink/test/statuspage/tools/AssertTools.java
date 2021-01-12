package ws.slink.test.statuspage.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertTools {

    public static void assertEmpty(Collection<? extends Object> value) {
        try {
            assertTrue(value.isEmpty());
        } catch (AssertionError e) {
            filterStackTrace(e);
            throw e;
        }
    }

    public static void assertEmpty(Optional<? extends Object> value) {
        try {
            assertFalse(value.isPresent());
        } catch (AssertionError e) {
            filterStackTrace(e);
            throw e;
        }
    }

    public static void assertNonEmpty(Optional<? extends Object> value) {
        try {
            assertTrue(value.isPresent());
        } catch (AssertionError e) {
            filterStackTrace(e);
            throw e;
        }
    }

    private static void filterStackTrace(AssertionError error) {
        StackTraceElement[] stackTrace = error.getStackTrace();
        if (null != stackTrace) {
            ArrayList<StackTraceElement> filteredStackTrace = new ArrayList<>();
            for (StackTraceElement e : stackTrace) {
                if (!AssertTools.class.getClass().getName().equals(e.getClassName())) {
                    filteredStackTrace.add(e);
                }
            }
            error.setStackTrace(filteredStackTrace.toArray(new StackTraceElement[0]));
        }
    }

}
