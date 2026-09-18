package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.SaveEmployeeRequestDto;
import com.pragma.powerup.application.dto.request.SaveUserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;

public interface IUserHandler {
    void saveOwner(SaveUserRequestDto saveUserRequestDto);
    void saveEmployee(SaveEmployeeRequestDto saveEmployeeRequestDto, Long idOwner);
    UserResponseDto getUserById(Long id);
}
