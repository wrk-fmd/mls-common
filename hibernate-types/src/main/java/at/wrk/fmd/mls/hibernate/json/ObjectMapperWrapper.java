package at.wrk.fmd.mls.hibernate.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.internal.util.SerializationHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Wrapper for the ObjectMapper used in the type descriptor
 * <p>
 * Adapted from https://github.com/vladmihalcea/hibernate-types/blob/master/hibernate-types-52/src/main/java/com/vladmihalcea/hibernate/type/util/ObjectMapperWrapper.java
 */
public class ObjectMapperWrapper {

    public static final ObjectMapperWrapper INSTANCE = new ObjectMapperWrapper();

    private final ObjectMapper objectMapper;

    private ObjectMapperWrapper() {
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    public JsonNode toJsonNode(Object value) {
        return objectMapper.valueToTree(value);
    }

    public <T> T fromString(String string, Type type) {
        try {
            return objectMapper.readValue(string, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("The given JSON string value '%s' cannot be transformed to instance of '%s'", string, type), e
            );
        }
    }

    public String toString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    String.format("The given object '%s' cannot be transformed to a JSON string", value), e
            );
        }
    }

    public <T> T fromBytes(byte[] value, Type type) {
        try {
            return objectMapper.readValue(value, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("The given JSON byte array cannot be transformed to instance of '%s'", type), e
            );
        }
    }

    public byte[] toBytes(Object value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    String.format("The given object '%s' cannot be transformed to a JSON byte array", value), e
            );
        }
    }

    public <T> T fromInputStream(InputStream inputStream, Type type) {
        try {
            return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    String.format("The given JSON input stream cannot be transformed to instance of '%s'", type), e
            );
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T clone(T value) {
        if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
            Object firstElement = ((Collection<?>) value).iterator().next();
            if (!(firstElement instanceof Serializable)) {
                JavaType type = TypeFactory.defaultInstance().constructParametricType(value.getClass(), firstElement.getClass());
                return this.fromBytes(this.toBytes(value), type);
            }
        }

        return value instanceof Serializable
                ? (T) SerializationHelper.clone((Serializable) value)
                : fromBytes(toBytes(value), value.getClass());
    }
}
