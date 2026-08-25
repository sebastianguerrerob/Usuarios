package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.application.dto.ClienteRequestDto;
import com.example.Usuarios.application.dto.EmpleadoRequestDto;
import com.example.Usuarios.application.dto.PropietarioRequestDto;
import com.example.Usuarios.application.mapper.IUsuarioRequestMapper;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.infrastructure.configuration.security.JwtService;
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
    private final JwtService jwtService;

    @PostMapping("/propietario")
    public ResponseEntity<Void> crearPropietario(@RequestBody PropietarioRequestDto propietarioRequestDto) {
        usuarioServicePort.crearPropietario(usuarioRequestMapper.toUsuarioFromPropietario(propietarioRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/empleado")
    public ResponseEntity<Void> crearEmpleado(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody EmpleadoRequestDto empleadoRequestDto) {
        String token = authHeader.substring(7);
        Long propietarioId = jwtService.extractUserId(token);
        usuarioServicePort.crearEmpleado(usuarioRequestMapper.toUsuarioFromEmpleado(empleadoRequestDto), propietarioId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/cliente")
    public ResponseEntity<Void> crearCliente(@RequestBody ClienteRequestDto clienteRequestDto) {
        usuarioServicePort.crearCliente(usuarioRequestMapper.toUsuarioFromCliente(clienteRequestDto));
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
