package com.example.Usuarios.application.handler.impl;

import com.example.Usuarios.application.dto.ClienteRequestDto;
import com.example.Usuarios.application.dto.EmpleadoRequestDto;
import com.example.Usuarios.application.dto.PropietarioRequestDto;
import com.example.Usuarios.application.handler.IUsuarioHandler;
import com.example.Usuarios.application.mapper.IUsuarioRequestMapper;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.infrastructure.configuration.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioHandlerImpl implements IUsuarioHandler {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioRequestMapper usuarioRequestMapper;
    private final JwtService jwtService;

    @Override
    public void crearPropietario(PropietarioRequestDto dto) {
        usuarioServicePort.crearPropietario(usuarioRequestMapper.toUsuarioFromPropietario(dto));
    }

    @Override
    public void crearEmpleado(EmpleadoRequestDto dto, String authHeader) {
        String token = authHeader.substring(7);
        Long propietarioId = jwtService.extractUserId(token);
        usuarioServicePort.crearEmpleado(usuarioRequestMapper.toUsuarioFromEmpleado(dto), propietarioId);
    }

    @Override
    public void crearCliente(ClienteRequestDto dto) {
        usuarioServicePort.crearCliente(usuarioRequestMapper.toUsuarioFromCliente(dto));
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioServicePort.obtenerUsuarioPorId(id);
    }
}
