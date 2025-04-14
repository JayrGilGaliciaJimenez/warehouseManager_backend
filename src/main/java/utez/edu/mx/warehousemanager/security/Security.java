package utez.edu.mx.warehousemanager.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utez.edu.mx.warehousemanager.jwt.JwtTokenFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Security {

    private final JwtTokenFilter jwtTokenFilter;

    Security(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF protection is disabled because this application is a stateless REST API
        // and uses JWT for authentication, which is not vulnerable to CSRF attacks.
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/reset-email").permitAll()
                        .requestMatchers("/api/auth/reset-password/**").permitAll()
                        .requestMatchers("/api/auth/activate-account/**").permitAll()
                        .requestMatchers("/api/user").hasRole(ADMIN)
                        .requestMatchers("/api/category").hasRole(USER)
                        .requestMatchers("/api/supplier").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/api/productEntry").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/api/stock").hasRole(USER)
                        .requestMatchers("/api/productOut").hasAnyRole(USER, ADMIN)
                        .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Value("${cors.allowed.origin}")
    private String allowedOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}