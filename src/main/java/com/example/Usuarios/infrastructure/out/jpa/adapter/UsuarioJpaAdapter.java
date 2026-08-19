package com.example.Usuarios.infrastructure.out.jpa.adapter;

import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import com.example.Usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import com.example.Usuarios.infrastructure.out.jpa.mapper.IUsuarioEntityMapper;
import com.example.Usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioJpaAdapter implements IUsuarioPersistencePort {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioEntityMapper usuarioEntityMapper;

    @Override
    public void guardarPropietario(Usuario usuario) {
        UsuarioEntity entity = usuarioEntityMapper.toEntity(usuario);
        usuarioRepository.save(entity);
    }
}