package com.example.Usuarios.domain.spi;

import com.example.Usuarios.domain.model.Usuario;

import java.util.Optional;

public interface IUsuarioPersistencePort {
    void guardarPropietario(Usuario Propietario);
    Optional<Usuario> obtenerUsuarioPorId(Long id);
}
