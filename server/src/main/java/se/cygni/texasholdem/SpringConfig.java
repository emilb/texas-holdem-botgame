package se.cygni.texasholdem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

@Configuration
public class SpringConfig {

    public @Bean
    EventBus eventBus() {

        return new EventBus();
    }
}
