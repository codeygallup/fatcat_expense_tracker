package com.codey.fatcat.config;

import com.codey.fatcat.JwtAuthenticationFilter;
import com.codey.fatcat.service.CustomUserDetailsService;
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

  public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
    this.customUserDetailsService = customUserDetailsService;
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
        .authorizeHttpRequests((authorize) -> authorize
                                   .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                                   .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                                   .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
//                                   .requestMatchers(HttpMethod.PATCH, "/users/*/role").hasRole("ADMIN")
                                   .requestMatchers("/accounts/**").permitAll()
                                   .requestMatchers("/transactions/**").permitAll()
//            .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                                   .anyRequest().authenticated()
        )
        .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
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


//http
//.csrf().disable()
//.authorizeHttpRequests(auth -> auth
//.requestMatchesrs("/api/users/register").permitAll()
//.requestMatchers("/api/users/login").permitAll()
//.anyRequest().authenticated()
//)
//.userDetailsService(customUserDetailsService)
//.formLogin()
//.loginProcessingUrl("/api/users/login")
//.successHandler((request, response, authentication) -> {
//response.setContentType("application/json");
//response.getWriter().write("{\"message\": \"Login successful\"}");
//})
//.failureHandler((request, response, exception) -> {
//response.setContentType("application/json");
//response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//response.getWriter().write("{\"error\": \"Invalid credientials\"}");
//});
//return http.build();
