package at.wrk.fmd.mls.hibernate.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

/**
 * Hibernate JavaTypeDescriptor for JSON columns
 * <p>
 * Adapted from https://github.com/vladmihalcea/hibernate-types/blob/master/hibernate-types-52/src/main/java/com/vladmihalcea/hibernate/type/json/internal/JsonTypeDescriptor.java
 */
class JsonJavaTypeDescriptor extends AbstractTypeDescriptor<Object> implements DynamicParameterizedType {

    private Type type;
    private final ObjectMapperWrapper objectMapperWrapper;

    public JsonJavaTypeDescriptor(ObjectMapperWrapper objectMapperWrapper) {
        super(Object.class, new MutableMutabilityPlan<>() {
            @Override
            protected Object deepCopyNotNull(Object value) {
                return objectMapperWrapper.clone(value);
            }
        });
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        type = ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();
    }

    @Override
    public boolean areEqual(Object one, Object another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        if (one instanceof String && another instanceof String) {
            return one.equals(another);
        }
        if (one instanceof Collection && another instanceof Collection) {
            return Objects.equals(one, another);
        }
        return objectMapperWrapper.toJsonNode(one).equals(objectMapperWrapper.toJsonNode(another));
    }

    @Override
    public String toString(Object value) {
        return objectMapperWrapper.toString(value);
    }

    @Override
    public Object fromString(String string) {
        return objectMapperWrapper.fromString(string, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(Object value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            // Return Java null for null values (not JSON "null")
            return null;
        }

        if (type.isAssignableFrom(String.class)) {
            // JSON string requested
            return (X) objectMapperWrapper.toString(value);
        }
        if (type.isAssignableFrom(BinaryStream.class) || byte[].class.isAssignableFrom(type)) {
            // Binary data requested
            return (X) new BinaryStreamImpl(objectMapperWrapper.toBytes(value));
        }
        if (type.isAssignableFrom(Blob.class)) {
            // Blob requested
            return (X) BlobProxy.generateProxy(objectMapperWrapper.toBytes(value));
        }

        if (type.isAssignableFrom(JsonNode.class)) {
            // Return JsonNode tree
            return (X) objectMapperWrapper.toJsonNode(value);
        }

        throw unknownUnwrap(type);
    }

    @Override
    public <X> Object wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (value instanceof Blob) {
            try {
                return objectMapperWrapper.fromInputStream(((Blob) value).getBinaryStream(), type);
            } catch (SQLException e) {
                throw new HibernateException("Unable to extract binary stream from Blob", e);
            }
        }

        if (value instanceof byte[]) {
            return objectMapperWrapper.fromBytes((byte[]) value, type);
        }

        if (value instanceof InputStream) {
            return objectMapperWrapper.fromInputStream((InputStream) value, type);
        }

        return objectMapperWrapper.fromString(value.toString(), type);
    }
}