package com.smart.workflow.config.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/03 16:51
 */
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getName().equals("admin")) {
            String[] roles = {"ROLE_ACTIVITI_USER", "GROUP_USER", "GROUP_ADMIN", "GROUP_TEST", "ROLE_ADMIN","ROLE_ACTIVITI_MANAGER"};
            return new UsernamePasswordAuthenticationToken("admin", "1", Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
