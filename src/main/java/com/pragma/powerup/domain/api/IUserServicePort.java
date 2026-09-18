package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserServicePort {
    void saveOwer(UserModel userModel);
    void saveEmployee(UserModel userModel, Long idOwner);
    UserModel getUserById(Long id);
}
