package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.UserUseCase;
import com.pragma.powerup.infrastructure.out.feign.IRestaurantFeignClient;
import com.pragma.powerup.infrastructure.out.feign.adapter.RestaurantFeignAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;
    private final IRestaurantFeignClient restaurantFeignClient;

    @Bean
    public IRestaurantValidationPort restaurantValidationPort(){
        return new RestaurantFeignAdapter(restaurantFeignClient);
    }
    @Bean
    public  IUserPersistencePort userPersistencePort(){
        return new UserJpaAdapter(userRepository,userEntityMapper);
    }
    @Bean
    public IUserServicePort userServicePort() {
        return new UserUseCase(userPersistencePort(),passwordEncoder(),restaurantValidationPort());
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}