package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.infrastructure.out.feign.dto.RestaurantFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="restaurant-service", url = "${adapters.restaurantfeignclient.url}")
public interface IRestaurantFeignClient {
    @GetMapping("/api/v1/restaurants/{id}")
    RestaurantFeignResponseDto getRestaurantById(@PathVariable Long id);
}
