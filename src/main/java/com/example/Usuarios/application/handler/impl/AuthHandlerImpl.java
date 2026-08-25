package com.example.Usuarios.application.handler.impl;

import com.example.Usuarios.application.dto.AuthRequestDto;
import com.example.Usuarios.application.dto.AuthResponseDto;
import com.example.Usuarios.application.handler.IAuthHandler;
import com.example.Usuarios.domain.api.IAuthServicePort;
import com.example.Usuarios.infrastructure.configuration.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthHandlerImpl implements IAuthHandler {

    private final IAuthServicePort authServicePort;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto login(AuthRequestDto dto) {
        String token = authServicePort.login(dto.getCorreo(), dto.getClave());
        return new AuthResponseDto(token);
    }

    @Override
    public Map<String, Object> validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Map.of("valid", false, "message", "Token no proporcionado");
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return Map.of("valid", false, "message", "Token inválido o expirado");
        }

        return Map.of(
                "valid", true,
                "correo", jwtService.extractCorreo(token),
                "rol", jwtService.extractRol(token),
                "userId", jwtService.extractUserId(token)
        );
    }
}
