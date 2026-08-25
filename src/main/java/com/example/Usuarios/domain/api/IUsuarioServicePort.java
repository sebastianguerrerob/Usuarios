package com.example.Usuarios.domain.api;

import com.example.Usuarios.domain.model.Usuario;

public interface IUsuarioServicePort {
    void crearPropietario(Usuario propietario);
    void crearEmpleado(Usuario empleado, Long propietarioId);
    void crearCliente(Usuario cliente);
    Usuario obtenerUsuarioPorId(Long id);
}
