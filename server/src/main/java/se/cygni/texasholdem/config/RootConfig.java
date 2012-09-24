package se.cygni.texasholdem.config;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;

@Configuration
@EnableCaching
@ComponentScan(basePackages = { "se.cygni.texasholdem" })
public class RootConfig {

    @Bean
    public EventBus eventBus() {

        return new EventBus();
    }

    @Bean(name = "applicationProperties")
    public PropertiesFactoryBean properties() {

        PropertiesFactoryBean pfb =
                new PropertiesFactoryBean();

        Resource[] resources = new ClassPathResource[]
                { new ClassPathResource("application.properties") };

        pfb.setLocations(resources);

        return pfb;
    }

    @Bean
    public CacheManager cacheManager() {
        // configure and return an implementation of Spring's CacheManager SPI
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
                Arrays.asList(
                        new ConcurrentMapCache("default"),
                        new ConcurrentMapCache("gamelog"),
                        new ConcurrentMapCache("statistics-actions"),
                        new ConcurrentMapCache("statistics-chips")
                        ));
        return cacheManager;
    }
}
