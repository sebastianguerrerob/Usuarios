package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.application.dto.ClienteRequestDto;
import com.example.Usuarios.application.dto.EmpleadoRequestDto;
import com.example.Usuarios.application.dto.PropietarioRequestDto;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.application.handler.IUsuarioHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final IUsuarioHandler usuarioHandler;

    @PostMapping("/propietario")
    public ResponseEntity<Void> crearPropietario(@RequestBody PropietarioRequestDto propietarioRequestDto) {
        usuarioHandler.crearPropietario(propietarioRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/empleado")
    public ResponseEntity<Void> crearEmpleado(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody EmpleadoRequestDto empleadoRequestDto) {
        usuarioHandler.crearEmpleado(empleadoRequestDto, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/cliente")
    public ResponseEntity<Void> crearCliente(@RequestBody ClienteRequestDto clienteRequestDto) {
        usuarioHandler.crearCliente(clienteRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioHandler.obtenerUsuarioPorId(id));
    }
}
