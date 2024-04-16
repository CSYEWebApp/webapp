package com.CreateAPI.WebApplication.Configure;

import com.CreateAPI.WebApplication.service.HealthService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    private HealthService healthService;
//    @Bean
//    public PasswordEncoder getPasswordEncoder() {
//        return new BCryptPasswordEncoder(10);
//    }

//    @Bean
//    public UserDetails userDetailsService() {
//        // Define user details (replace with your own user details)
//        UserDetails user = User.withUsername("username")
//                .password("password")
//                .roles("USER")
//                .build();
//        return user;
//    }

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests)->requests
                .requestMatchers(new AntPathRequestMatcher("/healthz")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/user","POST")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/verify/*","GET")).permitAll()
                .anyRequest()
                .authenticated());
        http.httpBasic(withDefaults())
                .authenticationProvider(customAuthenticationProvider)
                .exceptionHandling((exceptions)->exceptions.authenticationEntryPoint(customAuthenticationEntryPoint())
                );
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return(request, response, authException) -> {
            if(!healthService.checkDatabaseConnectivity()){
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                logger.error("Database connectivity issue - Service Unavailable");
            }else{
                System.out.println("Here in customAuthenticationEntryPoint");
                logger.debug("Unauthorized access detected");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }
}