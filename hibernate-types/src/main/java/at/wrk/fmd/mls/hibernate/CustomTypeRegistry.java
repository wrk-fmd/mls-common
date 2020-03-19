package at.wrk.fmd.mls.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class stores column type codes for custom column types
 * <p>
 * It is mainly used for enum types
 */
public class CustomTypeRegistry {

    private static final AtomicInteger code = new AtomicInteger(3000);
    private static final Map<Class<?>, Integer> types = new HashMap<>();

    /**
     * Add a column type for a class
     *
     * @param type The class to add
     * @return The type code assigned to the class
     */
    public static int addType(Class<?> type) {
        return types.computeIfAbsent(type, t -> code.incrementAndGet());
    }

    /**
     * Get the type code for a class
     *
     * @param type The class to query for
     * @return The type code assigned to the class, or null if none is set
     */
    public static Integer getTypeCode(Class<?> type) {
        return types.get(type);
    }
}
