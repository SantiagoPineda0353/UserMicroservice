package com.pragma.powerup.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {
    private AuthenticationUtils() {
    }
    public static Long getAuthenticatedUserId(){
        CustomUserDetails userDetails=
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}
