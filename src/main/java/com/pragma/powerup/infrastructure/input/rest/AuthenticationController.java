package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.AuthenticationRequestDto;
import com.pragma.powerup.application.dto.response.AuthenticationResponseDto;
import com.pragma.powerup.infrastructure.security.CustomUserDetails;
import com.pragma.powerup.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Loggin usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario Obtenido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos incorrectos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto request) {
        UsernamePasswordAuthenticationToken authToken=
            new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());

        var authentication = authenticationManager.authenticate(authToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponseDto(token));
    }
}
