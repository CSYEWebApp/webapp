package com.CreateAPI.WebApplication.Configure;

import com.CreateAPI.WebApplication.service.CustomUserDetailsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final Logger logger = LogManager.getLogger(CustomAuthenticationProvider.class);
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        System.out.println("Here in CustomAuthenticationProvider");
        logger.debug("Here in CustomAuthenticationProvider");
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        logger.debug("User "+email+" password"+password);

//        System.out.println("User "+email+" password"+password);
        UserDetails user = customUserDetailsService.loadUserByUsername(email);
        if(passwordEncoder.matches(password, user.getPassword())){
            System.out.println("Valid");
            logger.info("Valid Credentials");
            return new UsernamePasswordAuthenticationToken(user,password,user.getAuthorities());
        }else{
            logger.warn("Invalid credentials");
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
