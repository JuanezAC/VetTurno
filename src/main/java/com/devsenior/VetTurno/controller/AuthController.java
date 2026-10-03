package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.AuthResponse;
import com.devsenior.VetTurno.dto.LoginRequest;
import com.devsenior.VetTurno.dto.RegistroRequest;
import com.devsenior.VetTurno.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService s) {
        this.authService = s;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un usuario nuevo (recibe rol USER)")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.registrar(req));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener el token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(req));
    }
}
