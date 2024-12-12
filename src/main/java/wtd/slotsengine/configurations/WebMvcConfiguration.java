package wtd.slotsengine.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for customizing the Spring MVC framework.
 * Implements the {@link WebMvcConfigurer} interface to provide custom configurations
 * for web-related functionalities such as view resolution, resource handling,
 * message converters, CORS mappings, and others.
 * <p>
 * This class can be used to override or extend default behaviors provided by
 * the Spring Web MVC module.
 * <p>
 * Annotations:
 * - @Configuration indicates that this class provides configuration metadata to the application context.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
}