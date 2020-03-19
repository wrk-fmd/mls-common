package at.wrk.fmd.mls.hibernate.enums;

import at.wrk.fmd.mls.hibernate.AnnotationScanner;
import at.wrk.fmd.mls.hibernate.CustomPostgreSQLDialect;
import at.wrk.fmd.mls.hibernate.CustomTypeRegistry;
import org.hibernate.annotations.TypeDef;
import org.hibernate.boot.model.relational.AbstractAuxiliaryDatabaseObject;
import org.hibernate.dialect.Dialect;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is run by Hibernate on startup and creates the Postgres enum data types
 * <p>
 * CAVE: Data types are only created, not updated (i.e., if enums change the types have to be dropped or updated manually)
 */
class EnumTypeCreator extends AbstractAuxiliaryDatabaseObject {

    private static final String CREATE_SQL = ""
            + "DO $$"
            + "BEGIN\n"
            + "  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = '%s') THEN"
            + "    CREATE TYPE %s AS ENUM (%s);"
            + "  END IF;"
            + "END"
            + "$$;";

    private final AnnotationScanner scanner;

    public EnumTypeCreator(AnnotationScanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String[] sqlCreateStrings(Dialect dialect) {
        if (!(dialect instanceof CustomPostgreSQLDialect)) {
            // Do not create custom enum types if not on Postgres
            return new String[0];
        }

        CustomPostgreSQLDialect postgresDialect = (CustomPostgreSQLDialect) dialect;
        return getEnumClasses()
                .map(type -> createEnum(type, postgresDialect))
                .toArray(String[]::new);
    }

    @Override
    public String[] sqlDropStrings(Dialect dialect) {
        return new String[0];
    }

    private String createEnum(Class<? extends Enum<?>> type, CustomPostgreSQLDialect dialect) {
        // Register the enum in the type registry
        int code = CustomTypeRegistry.addType(type);

        // Register the type in the dialect
        String name = "e_" + type.getSimpleName().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
        dialect.registerColumnType(code, name);

        // Build the SQL string for creating the type
        String values = Arrays.stream(type.getEnumConstants())
                .map(val -> "'" + val + "'")
                .collect(Collectors.joining(","));
        return String.format(CREATE_SQL, name, name, values);
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<? extends Enum<?>>> getEnumClasses() {
        // Load all TypeDef annotations that are used for an enum
        return scanner.getAnnotations(TypeDef.class)
                .map(TypeDef::defaultForType)
                .filter(Enum.class::isAssignableFrom)
                .map(t -> (Class<? extends Enum<?>>) t);
    }
}
