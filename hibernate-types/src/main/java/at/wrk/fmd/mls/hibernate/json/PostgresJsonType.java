package at.wrk.fmd.mls.hibernate.json;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.ParameterizedType;

import java.util.Properties;

public class PostgresJsonType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType {

    public PostgresJsonType() {
        super(JsonSqlTypeDescriptor.INSTANCE, new JsonJavaTypeDescriptor(ObjectMapperWrapper.INSTANCE));
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((ParameterizedType) getJavaTypeDescriptor()).setParameterValues(parameters);
    }

    @Override
    public String getName() {
        return "jsonb";
    }
}
