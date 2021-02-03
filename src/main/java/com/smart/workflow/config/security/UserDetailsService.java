package com.smart.workflow.config.security;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/24 17:19
 */
@Configuration
@Slf4j
public class UserDetailsService {
//    @Bean
//    public org.springframework.security.core.userdetails.UserDetailsService myUserDetailsService() {
//
//        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//
//        String[][] usersGroupsAndRoles = {
//                {"user", "password", "ROLE_ACTIVITI_USER", "GROUP_start", "GROUP_admin"},
//                {"admin", "password", "ROLE_ACTIVITI_ADMIN", "ROLE_ADMIN", "GROUP_ADMIN", "GROUP_start"},
//                {"yga", "password", "ROLE_ACTIVITI_ADMIN", "GROUP_start", "GROUP_admin"},
//                {"supervisor", "password", "ROLE_ACTIVITI_ADMIN", "GROUP_start", "GROUP_supervisor"},
//                {"manager", "password", "ROLE_ACTIVITI_ADMIN", "GROUP_start", "GROUP_manager"},
//                {"generalManager", "password", "ROLE_ACTIVITI_ADMIN", "GROUP_start", "GROUP_generalManager"},
//        };
//
//        for (String[] user : usersGroupsAndRoles) {
//
//            List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(user, 2, user.length));
//            log.info("> Registering new user: " + user[0] + " with the following Authorities[" + authoritiesStrings + "]");
//            inMemoryUserDetailsManager.createUser(new User(user[0], passwordEncoder().encode(user[1]),
//                    authoritiesStrings.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
//        }
//
//        return inMemoryUserDetailsManager;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
