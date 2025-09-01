package com.codey.fatcat.config;

import com.codey.fatcat.service.CustomUserDetailsService;
import com.codey.fatcat.webtoken.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                        CustomAccessDeniedHandler customAccessDeniedHandler) {
    this.customUserDetailsService = customUserDetailsService;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests((authorize) -> authorize
                                   .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                                   .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                                   .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
//
                                   .requestMatchers("/accounts/**").permitAll()
                                   .requestMatchers("/transactions/**").permitAll()
//            .requestMatchers("/api/users/register", "/api/users/login").permitAll()
//            // Public endpoints
//                                   .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
//                                   .requestMatchers(HttpMethod.POST, "/user/login").permitAll()
//            // Admin only endpoints
//                                   .requestMatchers(HttpMethod.PATCH, "/users/*/role").hasRole("ADMIN")
//                                   .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
//            // Protected endpoints with authentication
//                                   .requestMatchers("/accounts/**").authenticated()
//                                   .requestMatchers("/transactions/**").authenticated()
//                                   .requestMatchers("/users/**").authenticated()
            // Default to authenticated
                                   .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/home")
            .invalidateHttpSession(true))
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(authenticationProvider());
  }
}
