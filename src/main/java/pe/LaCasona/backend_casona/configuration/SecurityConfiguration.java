package pe.LaCasona.backend_casona.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(crsf -> crsf.disable()) // Desabilitar la falsificación de solucitudes.
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

}
