package se.cygni.texasholdem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(value = "classpath:spring-security-context.xml")
public class SecurityConfig {
}
