package com.example.Usuarios.domain.spi;

public interface ITokenGeneratorPort {
    String generateToken(String correo, String rol, Long userId);
}
