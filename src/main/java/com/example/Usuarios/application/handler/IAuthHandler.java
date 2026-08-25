package com.example.Usuarios.application.handler;

import com.example.Usuarios.application.dto.AuthRequestDto;
import com.example.Usuarios.application.dto.AuthResponseDto;

import java.util.Map;

public interface IAuthHandler {
    AuthResponseDto login(AuthRequestDto dto);
    Map<String, Object> validateToken(String authHeader);
}
