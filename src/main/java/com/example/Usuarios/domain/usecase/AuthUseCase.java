package com.example.Usuarios.domain.usecase;

import com.example.Usuarios.domain.api.IAuthServicePort;
import com.example.Usuarios.domain.exception.DomainException;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IPasswordHandler;
import com.example.Usuarios.domain.spi.ITokenGeneratorPort;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import com.example.Usuarios.domain.util.DomainConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordHandler passwordHandler;
    private final ITokenGeneratorPort tokenGeneratorPort;

    @Override
    public String login(String correo, String clave) {
        Usuario usuario = usuarioPersistencePort.obtenerUsuarioPorCorreo(correo)
                .orElseThrow(() -> new DomainException(DomainConstants.CREDENCIALES_INVALIDAS));

        if (!passwordHandler.matches(clave, usuario.getClave())) {
            throw new DomainException(DomainConstants.CREDENCIALES_INVALIDAS);
        }

        String rolNombre = usuarioPersistencePort.obtenerNombreRolPorUsuarioId(usuario.getId());
        return tokenGeneratorPort.generateToken(usuario.getCorreo(), rolNombre, usuario.getId());
    }
}
