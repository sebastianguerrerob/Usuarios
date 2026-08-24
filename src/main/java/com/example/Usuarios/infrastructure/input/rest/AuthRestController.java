package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.application.dto.AuthRequestDto;
import com.example.Usuarios.application.dto.AuthResponseDto;
import com.example.Usuarios.application.handler.IPasswordHandler;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import com.example.Usuarios.infrastructure.configuration.security.JwtService;
import com.example.Usuarios.infrastructure.out.jpa.entity.RolEntity;
import com.example.Usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import com.example.Usuarios.infrastructure.out.jpa.mapper.IUsuarioEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordHandler passwordHandler;
    private final JwtService jwtService;
    private final IUsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        Usuario usuario = usuarioPersistencePort.obtenerUsuarioPorCorreo(authRequestDto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordHandler.matches(authRequestDto.getClave(), usuario.getClave())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String rolNombre = usuarioRepository.findByCorreo(authRequestDto.getCorreo())
                .map(entity -> entity.getRol().getNombre())
                .orElse("UNKNOWN");

        String token = jwtService.generateToken(usuario.getCorreo(), rolNombre, usuario.getId());
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
