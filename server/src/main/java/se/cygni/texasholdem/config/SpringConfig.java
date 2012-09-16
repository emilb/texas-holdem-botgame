package se.cygni.texasholdem.config;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class SpringConfig {

    public
    @Bean
    EventBus eventBus() {

        return new EventBus();
    }

    public
    @Bean(name = "applicationProperties")
    PropertiesFactoryBean properties() {

        PropertiesFactoryBean pfb =
                new PropertiesFactoryBean();

        Resource[] resources = new ClassPathResource[]
                { new ClassPathResource("application.properties") };

        pfb.setLocations(resources);

        return pfb;
    }
}
