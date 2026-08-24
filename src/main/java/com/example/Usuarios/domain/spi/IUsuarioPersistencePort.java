package com.example.Usuarios.domain.spi;

import com.example.Usuarios.domain.model.Usuario;

import java.util.Optional;

public interface IUsuarioPersistencePort {
    void guardarPropietario(Usuario Propietario);
    void guardarEmpleado(Usuario empleado);
    void guardarCliente(Usuario cliente);
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    Optional<Usuario> obtenerUsuarioPorCorreo(String correo);
}
