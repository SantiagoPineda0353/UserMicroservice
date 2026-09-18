package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.exception.UserNotOwnerRestaurantException;
import com.pragma.powerup.domain.spi.IRestaurantValidationPort;
import com.pragma.powerup.infrastructure.out.feign.IRestaurantFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantFeignAdapter implements IRestaurantValidationPort {

    private final IRestaurantFeignClient restaurantFeignClient;

    @Override
    public Long getRestaurantOwnerId(Long idRestaurant) {
        try{
            return restaurantFeignClient.getRestaurantById(idRestaurant).getIdOwner();
        }catch (FeignException.NotFound ex){
            throw new UserNotOwnerRestaurantException();
        }
    }
}
