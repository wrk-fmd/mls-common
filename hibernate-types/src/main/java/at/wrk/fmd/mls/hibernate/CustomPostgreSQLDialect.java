package at.wrk.fmd.mls.hibernate;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

/**
 * This class extends the default Hibernate Postgres dialect and adds additional column type definitions
 */
public class CustomPostgreSQLDialect extends PostgreSQL10Dialect {

    public CustomPostgreSQLDialect() {
        super();
        this.registerColumnType(Types.OTHER, "json");
        this.registerColumnType(Types.OTHER, "jsonb");
        this.registerHibernateType(Types.OTHER, "json");
        this.registerHibernateType(Types.OTHER, "jsonb");
    }

    @Override
    public void registerColumnType(int code, String name) {
        super.registerColumnType(code, name);
    }
}
