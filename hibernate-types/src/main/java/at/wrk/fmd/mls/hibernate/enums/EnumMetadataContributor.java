package at.wrk.fmd.mls.hibernate.enums;

import at.wrk.fmd.mls.hibernate.AnnotationScanner;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.boot.spi.MetadataBuilderImplementor;

/**
 * This class adds custom metadata to the builder used by Hibernate
 */
public class EnumMetadataContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        // Build an annotation scanner based on the class list already built by Hibernate
        BootstrapContext context = metadataBuilder.unwrap(MetadataBuilderImplementor.class).getBootstrapContext();
        AnnotationScanner scanner = new AnnotationScanner(context.getScanEnvironment().getExplicitlyListedClassNames());

        // Add an auxiliary database object that creates the database enum types
        metadataBuilder.applyAuxiliaryDatabaseObject(new EnumTypeCreator(scanner));
    }
}
