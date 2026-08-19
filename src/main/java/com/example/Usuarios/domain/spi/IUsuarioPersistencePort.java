package com.example.Usuarios.domain.spi;

import com.example.Usuarios.domain.model.Usuario;

public interface IUsuarioPersistencePort {
    void guardarPropietario(Usuario Propietario);
}
