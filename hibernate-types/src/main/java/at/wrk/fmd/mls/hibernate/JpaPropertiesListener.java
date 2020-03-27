package at.wrk.fmd.mls.hibernate;

import at.wrk.fmd.mls.hibernate.enums.EnumMetadataContributor;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class JpaPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Properties properties = new Properties();
        properties.put("spring.jpa.open-in-view", true);
        properties.put("spring.jpa.properties.hibernate.dialect", CustomPostgreSQLDialect.class.getName());
        properties.put("spring.jpa.properties.hibernate.metadata_builder_contributor", EnumMetadataContributor.class.getName());
        properties.put("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation", true);
        properties.put("spring.jpa.properties.hibernate.jdbc.time_zone", "UTC");

        event.getEnvironment().getPropertySources().addFirst(new PropertiesPropertySource("jpaProperties", properties));
    }
}