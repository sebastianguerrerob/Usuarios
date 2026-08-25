package com.example.Usuarios.domain.spi;

public interface IPasswordHandler {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
