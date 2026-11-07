package event_management.event.config;

import event_management.common.security.AuthServiceClient;
import event_management.common.security.JwtAuthenticationFilter;
import event_management.common.security.JwtSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(sessions ->
                        sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/eventos-api-docs/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/events/*/participantes/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/events/*/inscrever").permitAll()
                                .requestMatchers(HttpMethod.GET, "/events/**", "/categories/**").permitAll()
                                .requestMatchers("/categories/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public JwtSupport jwtSupport(@Value("${jwt.secret}") String secret) {
        return new JwtSupport(secret);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtSupport jwtSupport, @Value("${auth.url}") String authUrl) {
        return new JwtAuthenticationFilter(jwtSupport, new AuthServiceClient(authUrl)::isRevogado);
    }
}
