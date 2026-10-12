package com.mercado.altiplano.controller;

import com.mercado.altiplano.dto.LoginRequest;
import com.mercado.altiplano.dto.LoginResponse;
import com.mercado.altiplano.dto.RegistroRequest;
import com.mercado.altiplano.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @PostMapping("/registro")
    public ResponseEntity<LoginResponse> registro(
            @RequestBody RegistroRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registrar(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization", required = false) String autorizacion
    ) {
        authService.cerrarSesion(autorizacion);
        return ResponseEntity.noContent().build();
    }
}
