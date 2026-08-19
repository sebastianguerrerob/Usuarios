package com.example.Usuarios.application.handler;

public interface IPasswordHandler {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
