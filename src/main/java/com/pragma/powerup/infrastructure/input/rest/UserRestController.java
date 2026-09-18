package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.SaveEmployeeRequestDto;
import com.pragma.powerup.application.dto.request.SaveUserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.infrastructure.security.AuthenticationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserHandler userHandler;
    @Operation(summary = "Crear cuenta propietario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propietario creado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Correo ya registrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos incorrectos", content = @Content)
    })
    @PostMapping("/owner")
    public ResponseEntity<Void> saveOwner(@RequestBody SaveUserRequestDto saveUserRequestDto) {
        userHandler.saveOwner(saveUserRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Crear cuenta empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Correo ya registrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos incorrectos", content = @Content)
    })
    @PostMapping("/employee")
    public ResponseEntity<Void> saveEmployee(@RequestBody SaveEmployeeRequestDto saveEmployeeRequestDto) {
        Long idOwner = AuthenticationUtils.getAuthenticatedUserId();
        userHandler.saveEmployee(saveEmployeeRequestDto,idOwner);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userHandler.getUserById(id));
    }
}
