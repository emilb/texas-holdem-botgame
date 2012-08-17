package se.cygni.texasholdem.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    public
    @Bean
    EventBus eventBus() {

        return new EventBus();
    }
}
