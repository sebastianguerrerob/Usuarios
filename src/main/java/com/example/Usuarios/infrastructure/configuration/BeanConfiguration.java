package com.example.Usuarios.infrastructure.configuration;

import com.example.Usuarios.application.handler.IPasswordHandler;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import com.example.Usuarios.domain.usecase.UsuarioUseCase;
import com.example.Usuarios.infrastructure.out.jpa.adapter.UsuarioJpaAdapter;
import com.example.Usuarios.infrastructure.out.jpa.mapper.IUsuarioEntityMapper;
import com.example.Usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioEntityMapper usuarioEntityMapper;
    private final IPasswordHandler passwordHandler;

    @Bean
    public IUsuarioPersistencePort usuarioPersistencePort() {
        return new UsuarioJpaAdapter(usuarioRepository, usuarioEntityMapper);
    }

    @Bean
    public IUsuarioServicePort usuarioServicePort() {
        return new UsuarioUseCase(usuarioPersistencePort(), passwordHandler);
    }
}