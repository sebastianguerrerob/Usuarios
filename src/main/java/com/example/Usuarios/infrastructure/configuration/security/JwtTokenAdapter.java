package com.example.Usuarios.infrastructure.configuration.security;

import com.example.Usuarios.domain.spi.ITokenGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements ITokenGeneratorPort {

    private final JwtService jwtService;

    @Override
    public String generateToken(String correo, String rol, Long userId) {
        return jwtService.generateToken(correo, rol, userId);
    }
}
