package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.application.dto.AuthRequestDto;
import com.example.Usuarios.application.dto.AuthResponseDto;
import com.example.Usuarios.domain.api.IAuthServicePort;
import com.example.Usuarios.infrastructure.configuration.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final IAuthServicePort authServicePort;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        String token = authServicePort.login(authRequestDto.getCorreo(), authRequestDto.getClave());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Token no proporcionado"));
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Token inválido o expirado"));
        }

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "correo", jwtService.extractCorreo(token),
                "rol", jwtService.extractRol(token),
                "userId", jwtService.extractUserId(token)
        ));
    }
}
