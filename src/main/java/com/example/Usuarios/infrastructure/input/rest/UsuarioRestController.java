package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.application.dto.UsuarioRequestDto;
import com.example.Usuarios.application.mapper.IUsuarioRequestMapper;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioRequestMapper usuarioRequestMapper;

    @PostMapping("/propietario")
    public ResponseEntity<Void> crearPropietario(@RequestBody UsuarioRequestDto usuarioRequestDto) {
        usuarioServicePort.crearPropietario(usuarioRequestMapper.toUsuario(usuarioRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/empleado")
    public ResponseEntity<Void> crearEmpleado(@RequestBody UsuarioRequestDto usuarioRequestDto) {
        usuarioServicePort.crearEmpleado(usuarioRequestMapper.toUsuario(usuarioRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                usuarioServicePort.obtenerUsuarioPorId(id)
        );
    }
}