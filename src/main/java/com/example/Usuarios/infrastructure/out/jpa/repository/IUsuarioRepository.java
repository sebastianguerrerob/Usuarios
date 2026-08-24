package com.example.Usuarios.infrastructure.out.jpa.repository;

import com.example.Usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByCorreo(String correo);
}