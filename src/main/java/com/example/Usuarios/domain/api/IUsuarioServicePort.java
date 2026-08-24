package com.example.Usuarios.domain.api;

import com.example.Usuarios.domain.model.Usuario;

public interface IUsuarioServicePort {
    void crearPropietario(Usuario propietario);
    void crearEmpleado(Usuario empleado);
    Usuario obtenerUsuarioPorId(Long id);
}
