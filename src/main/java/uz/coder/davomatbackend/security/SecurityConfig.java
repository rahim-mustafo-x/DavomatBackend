package uz.coder.davomatbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import uz.coder.davomatbackend.jwt.JwtAuthFilter;
import uz.coder.davomatbackend.jwt.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userService;

    public SecurityConfig(JwtService jwtService,@Lazy UserDetailsService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService, userService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ❌ CSRF o'chiq (JWT uchun shart)
                .csrf(AbstractHttpConfigurer::disable)

                // 🌐 CORS configuration
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(java.util.List.of(
                        "http://localhost:5173",
                        "http://localhost:3000", 
                        "http://localhost:8080"
                    ));
                    corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))

                // 🔐 Session yo'q (STATLESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔑 Authorization qoidalari
                .authorizeHttpRequests(auth -> auth
                        // 🔓 AUTH (login, register)
                        .requestMatchers("/auth/**").permitAll()

                        // 🔓 Telegram API public (tokensiz ishlaydi)
                        .requestMatchers("/api/telegram/**").permitAll()
                        .requestMatchers("/api/contact/**").permitAll()

                        // 🔓 WebSocket endpoints
                        .requestMatchers("/ws/**").permitAll()

                        // 🔓 SWAGGER - Public (no login required)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/websocket-test.html"
                        ).permitAll()

                        // 🔐 Qolgan API faqat TOKEN bilan
                        .requestMatchers("/api/**").authenticated()

                        // qolganlar
                        .anyRequest().permitAll()
                )


                // 🔍 JWT FILTER
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔐 Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔑 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
