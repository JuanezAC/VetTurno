package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.AuthResponse;
import com.devsenior.VetTurno.dto.LoginRequest;
import com.devsenior.VetTurno.dto.RegistroRequest;
import com.devsenior.VetTurno.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService s) {
        this.authService = s;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.registrar(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(req));
    }
}
