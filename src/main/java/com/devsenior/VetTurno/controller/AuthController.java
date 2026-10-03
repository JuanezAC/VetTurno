package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.AuthResponse;
import com.devsenior.VetTurno.dto.LoginRequest;
import com.devsenior.VetTurno.dto.RegistroRequest;
import com.devsenior.VetTurno.exception.EmailYaRegistradoException;
import com.devsenior.VetTurno.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService s) {
        this.authService = s;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.registrar(req));
        } catch (EmailYaRegistradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.login(req));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
