package at.wrk.fmd.mls.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This scanner is used for reading annotations based on a pre-built list of classes and packages
 */
public class AnnotationScanner {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Collection<AnnotatedElement> annotatedElements;

    /**
     * Build a scanner instance
     *
     * @param classAndPackageNames A collection of classes and packages that should be scanned
     */
    public AnnotationScanner(Collection<String> classAndPackageNames) {
        this.annotatedElements = classAndPackageNames.stream()
                .map(this::loadClassOrPackage)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private AnnotatedElement loadClassOrPackage(String name) {
        try {
            // First, try it as a class name
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            // Not a class
        }

        try {
            // Second, try it as a package name
            return Class.forName(name + ".package-info").getPackage();
        } catch (ClassNotFoundException e) {
            // Not a package
        }

        LOG.warn("Could not load class or package for {}", name);
        return null;
    }

    /**
     * Get all annotations of the given type from the pre-built list of classes and packages
     *
     * @param annotationType The annotation class to scan for
     * @param <A> The annotation type
     * @return A stream of annotations
     */
    public <A extends Annotation> Stream<A> getAnnotations(Class<A> annotationType) {
        return annotatedElements.stream().flatMap(e -> Arrays.stream(e.getAnnotationsByType(annotationType)));
    }
}
