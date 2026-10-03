package com.devsenior.VetTurno.service;

import com.devsenior.VetTurno.dto.AuthResponse;
import com.devsenior.VetTurno.dto.LoginRequest;
import com.devsenior.VetTurno.dto.RegistroRequest;
import com.devsenior.VetTurno.exception.EmailYaRegistradoException;
import com.devsenior.VetTurno.model.Rol;
import com.devsenior.VetTurno.model.Usuario;
import com.devsenior.VetTurno.repository.UsuarioRepository;
import com.devsenior.VetTurno.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(RegistroRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new EmailYaRegistradoException(req.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(req.getEmail());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setRol(Rol.USER);
        usuarioRepository.save(usuario);

        return new AuthResponse(jwtService.generarToken(usuario.getEmail()));
    }

    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        return new AuthResponse(jwtService.generarToken(req.getEmail()));
    }
}
