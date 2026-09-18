package com.pragma.powerup.domain.exception;

public class UserNotOwnerRestaurantException extends DomainException {
    public UserNotOwnerRestaurantException() {
        super("El restaurante indicado no pertenece al propietario autenticado");
    }
}
