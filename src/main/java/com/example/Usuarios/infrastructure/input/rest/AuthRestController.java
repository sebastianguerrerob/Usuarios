package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.application.dto.AuthRequestDto;
import com.example.Usuarios.application.dto.AuthResponseDto;
import com.example.Usuarios.application.handler.IAuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final IAuthHandler authHandler;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authHandler.login(authRequestDto));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = authHandler.validateToken(authHeader);
        boolean valid = Boolean.TRUE.equals(result.get("valid"));
        return valid
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(401).body(result);
    }
}
