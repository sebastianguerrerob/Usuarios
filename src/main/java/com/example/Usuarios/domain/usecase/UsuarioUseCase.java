package com.example.Usuarios.domain.usecase;

import com.example.Usuarios.application.handler.IPasswordHandler;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.exception.DomainException;
import com.example.Usuarios.domain.model.RolEnum;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IRestauranteValidationPort;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import com.example.Usuarios.domain.util.DomainConstants;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordHandler passwordHandler;
    private final IRestauranteValidationPort restauranteValidationPort;

    @Override
    public void crearPropietario(Usuario propietario) {
        if (Period.between(propietario.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new DomainException(DomainConstants.USUARIO_MAYOR_DE_EDAD);
        }
        validarCamposComunes(propietario);
        propietario.setClave(passwordHandler.encode(propietario.getClave()));
        propietario.setRolId(RolEnum.PROPIETARIO.getId());
        usuarioPersistencePort.guardarPropietario(propietario);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioPersistencePort.obtenerUsuarioPorId(id)
                .orElseThrow(() ->
                        new DomainException(DomainConstants.USUARIO_NO_ENCONTRADO + id)
                );
    }

    @Override
    public void crearEmpleado(Usuario empleado, Long propietarioId) {
        if (empleado.getRestauranteId() == null) {
            throw new DomainException(DomainConstants.EMPLEADO_SIN_RESTAURANTE);
        }
        if (!restauranteValidationPort.validarPropietarioRestaurante(empleado.getRestauranteId(), propietarioId)) {
            throw new DomainException(DomainConstants.RESTAURANTE_NO_PERTENECE);
        }
        validarCamposComunes(empleado);
        empleado.setClave(passwordHandler.encode(empleado.getClave()));
        empleado.setRolId(RolEnum.EMPLEADO.getId());
        usuarioPersistencePort.guardarEmpleado(empleado);
    }

    @Override
    public void crearCliente(Usuario cliente) {
        validarCamposComunes(cliente);
        cliente.setClave(passwordHandler.encode(cliente.getClave()));
        cliente.setRolId(RolEnum.CLIENTE.getId());
        usuarioPersistencePort.guardarCliente(cliente);
    }

    private void validarCamposComunes(Usuario usuario) {
        if (!usuario.getNumeroDocumento().matches(DomainConstants.REGEX_DOCUMENTO)) {
            throw new DomainException(DomainConstants.DOCUMENTO_NUMERICO);
        }
        if (!usuario.getCelular().matches(DomainConstants.REGEX_CELULAR)) {
            throw new DomainException(DomainConstants.CELULAR_INVALIDO);
        }
        if (!usuario.getCorreo().matches(DomainConstants.REGEX_CORREO)) {
            throw new DomainException(DomainConstants.CORREO_INVALIDO);
        }
    }
}
