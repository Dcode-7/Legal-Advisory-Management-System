package com.inn.legal.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration  //Indicates that this class provides Spring configuration.
@EnableWebSecurity  // Enables Spring Security’s web security support.
public class SecurityConfig {

    @Autowired
    ClientUserDetailsService clientUserDetailsService;

    @Autowired
    JwtFilter jwtFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://127.0.0.1:5500/"); // Replace '*' with specific origin for security
        configuration.addAllowedHeader("*"); // You may specify individual headers like "Content-Type"
        configuration.addAllowedMethod("*"); // Allowed HTTP methods like GET, POST, etc.
        configuration.addExposedHeader("Authorization"); // Include this if you need to expose any headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() { //to encode passwords securely.
        return new BCryptPasswordEncoder(); // for password hashing.
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception { //Configures the AuthenticationManager
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(clientUserDetailsService).passwordEncoder(passwordEncoder()); //Sets the custom UserDetailsService (clientUserDetailsService) and the password encoder.
        return authenticationManagerBuilder.build(); //Returns the configured AuthenticationManager.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //Configures the security filter chain for HTTP requests.
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user/signup", "/user/search","/user/update-profile","/user/filterLawyersBySpecialization","/user/raise-case").permitAll() //Permits public access
                        .anyRequest().authenticated() //Requires authentication for all other requests.
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //no session will be created or stored
                );

        // Add JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //Adds the custom JWT filter

        return http.build();
    }


}
