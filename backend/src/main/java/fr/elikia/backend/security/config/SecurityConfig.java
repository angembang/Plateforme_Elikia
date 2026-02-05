package fr.elikia.backend.security.config;

import fr.elikia.backend.security.jwt.JwtAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Global security configuration of the application.

 * This configuration:
 * - Disables CSRF protection (JWT-based stateless API)
 * - Disables CORS (handled elsewhere or open API during development)
 * - Registers the JWT authentication interceptor

 * This class does NOT use Spring Security authentication filters,
 * but relies on a custom HandlerInterceptor for JWT validation.
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    // Custom interceptor responsible for JWT authentication and authorization
    private final JwtAuthRequestInterceptor jwtAuthRequestInterceptor;

    /**
     * Constructor injection of the JWT interceptor
     *
     * @param jwtAuthRequestInterceptor interceptor handling JWT validation
     */
    public SecurityConfig(JwtAuthRequestInterceptor jwtAuthRequestInterceptor) {
        this.jwtAuthRequestInterceptor = jwtAuthRequestInterceptor;
    }

    /**
     * Spring Security filter chain configuration.
     *
     * @param http HttpSecurity object
     * @return configured SecurityFilterChain
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable CSRF protection (not needed for stateless JWT APIs)
        http.csrf(AbstractHttpConfigurer::disable);

        // Active CORS at Spring Security level
        http.cors(Customizer.withDefaults());

        return http.build();
    }


    /**
     * Cors security config
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Registers the JWT authentication interceptor
     * so it is executed before controller methods.
     *
     * @param registry interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthRequestInterceptor);
    }


}
