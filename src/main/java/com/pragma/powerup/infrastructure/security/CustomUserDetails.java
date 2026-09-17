package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final transient UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Long getId(){
        return userEntity.getId();
    }

    public Long getIdRole(){
        return userEntity.getIdRole();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RoleEnum role= RoleEnum.fromId(userEntity.getIdRole());
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
