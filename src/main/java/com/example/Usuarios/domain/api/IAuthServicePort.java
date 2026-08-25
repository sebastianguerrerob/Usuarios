package com.example.Usuarios.domain.api;

public interface IAuthServicePort {
    String login(String correo, String clave);
}
