package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.exception.UserNotOwnerRestaurantException;
import com.pragma.powerup.infrastructure.out.feign.IRestaurantFeignClient;
import com.pragma.powerup.infrastructure.out.feign.dto.RestaurantFeignResponseDto;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantFeignAdapterTest {

    @Mock
    private IRestaurantFeignClient restaurantFeignClient;

    private RestaurantFeignAdapter restaurantFeignAdapter;

    @BeforeEach
    void setUp(){
        restaurantFeignAdapter= new RestaurantFeignAdapter(restaurantFeignClient);
    }

    @Test
    void getRestaurantOwnerId_whenRestaurantExists_thenReturnOwnerId(){
        RestaurantFeignResponseDto response= new RestaurantFeignResponseDto();
        response.setId(5L);
        response.setIdOwner(10L);
        when(restaurantFeignClient.getRestaurantById(5L))
                .thenReturn(response);
        Long ownerId=restaurantFeignAdapter.getRestaurantOwnerId(5L);
        assertEquals(10L,ownerId);
    }

    @Test
    void getRestaurantOwnerId_whenRestaurantNonExistent_thenThrowsException(){
        Request request= Request.create(Request.HttpMethod.GET,"/api/v1/restaurants/999",
                Collections.emptyMap(),null, StandardCharsets.UTF_8,null);
        FeignException.NotFound notFound= new FeignException.NotFound("Not Found",request,null,null);
        when(restaurantFeignClient.getRestaurantById(999L))
                .thenThrow(notFound);
        assertThrows(UserNotOwnerRestaurantException.class, () ->restaurantFeignAdapter.getRestaurantOwnerId(999L));
    }
}
