package com.smart.workflow.config.security;

import com.smart.workflow.config.security.handler.CusAuthenticationFailureHandler;
import com.smart.workflow.config.security.handler.CusAuthenticationSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/03 11:12
 */
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private CusAuthenticationSuccessHandler cusAuthenticationSuccessHandler;

    @Autowired
    private CusAuthenticationFailureHandler cusAuthenticationFailureHandler;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf().ignoringAntMatchers("/login").csrfTokenRepository(csrfTokenRepository)
                .csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .successHandler(cusAuthenticationSuccessHandler)
                .failureHandler(cusAuthenticationFailureHandler)
                .loginPage("http://localhost:8000/user/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .and()
                .logout().deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .httpBasic();
        http
                .headers()
                .frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
