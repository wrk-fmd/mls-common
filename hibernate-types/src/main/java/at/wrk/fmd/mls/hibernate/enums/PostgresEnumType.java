package at.wrk.fmd.mls.hibernate.enums;

import at.wrk.fmd.mls.hibernate.CustomTypeRegistry;
import org.hibernate.annotations.TypeDef;
import org.hibernate.metamodel.model.convert.internal.NamedEnumValueConverter;
import org.hibernate.metamodel.model.convert.spi.EnumValueConverter;
import org.hibernate.type.EnumType;
import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * This class extends the standard Hibernate type for enums and uses the specific enum type available in Postgres instead of VARCHAR
 * <p>
 * Use it by adding a {@link TypeDef} annotation on either the entity or in package-info like this:
 * {@code @TypeDef(typeClass = PostgresEnumType.class, defaultForType = EnumClass.class)}
 *
 * @param <E> The enum class this type is used for
 */
public class PostgresEnumType<E extends Enum<E>> extends EnumType<E> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void setParameterValues(Properties parameters) {
        super.setParameterValues(parameters);

        // This is somewhat ugly, but the only alternative would be to override every method separately by copying from EnumType
        try {
            // Make the private converter field accessible
            Field converterField = EnumType.class.getDeclaredField("enumValueConverter");
            converterField.setAccessible(true);

            // Load the existing converter
            @SuppressWarnings("unchecked")
            EnumValueConverter<E, ?> oldConverter = (EnumValueConverter<E, ?>) converterField.get(this);

            // Create a new converter and replace the old one
            EnumValueConverter<E, String> newConverter = new PostgresEnumValueConverter<>(oldConverter.getJavaDescriptor());
            converterField.set(this, newConverter);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOG.error("Failed setting the PostgresEnumValueConverter");
        }
    }

    private static class PostgresEnumValueConverter<E extends Enum<E>> extends NamedEnumValueConverter<E> {

        public PostgresEnumValueConverter(EnumJavaTypeDescriptor<E> enumJavaDescriptor) {
            super(enumJavaDescriptor);
        }

        @Override
        public int getJdbcTypeCode() {
            // Get the registered type code for the enum, use the standard converter's type as a fallback
            Integer code = CustomTypeRegistry.getTypeCode(getJavaDescriptor().getJavaType());
            return code != null ? code : super.getJdbcTypeCode();
        }
    }
}
