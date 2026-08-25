package com.example.Usuarios.application.handler;

import com.example.Usuarios.application.dto.ClienteRequestDto;
import com.example.Usuarios.application.dto.EmpleadoRequestDto;
import com.example.Usuarios.application.dto.PropietarioRequestDto;
import com.example.Usuarios.domain.model.Usuario;

public interface IUsuarioHandler {
    void crearPropietario(PropietarioRequestDto dto);
    void crearEmpleado(EmpleadoRequestDto dto, String authHeader);
    void crearCliente(ClienteRequestDto dto);
    Usuario obtenerUsuarioPorId(Long id);
}
