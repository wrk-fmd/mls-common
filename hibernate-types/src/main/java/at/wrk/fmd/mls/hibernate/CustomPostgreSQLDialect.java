package at.wrk.fmd.mls.hibernate;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

/**
 * This class extends the default Hibernate Postgres dialect and adds additional column type definitions
 */
public class CustomPostgreSQLDialect extends PostgreSQL10Dialect {

    public CustomPostgreSQLDialect() {
        super();
        this.registerColumnType(Types.JAVA_OBJECT, "json");
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
        this.registerHibernateType(Types.JAVA_OBJECT, "json");
        this.registerHibernateType(Types.JAVA_OBJECT, "jsonb");
    }

    @Override
    public void registerColumnType(int code, String name) {
        super.registerColumnType(code, name);
    }
}
