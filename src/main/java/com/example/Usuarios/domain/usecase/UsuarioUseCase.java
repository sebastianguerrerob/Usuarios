package com.example.Usuarios.domain.usecase;

import com.example.Usuarios.application.handler.IPasswordHandler;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.exception.DomainException;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import com.example.Usuarios.domain.spi.IRestauranteValidationPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordHandler passwordHandler;
    private final IRestauranteValidationPort restauranteValidationPort;
    private static final String REGEX_DOCUMENTO = "\\d+";
    private static final String REGEX_CELULAR = "^\\+?\\d{1,12}$";
    private static final String REGEX_CORREO = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public void crearPropietario(Usuario propietario) {
        if (Period.between(propietario.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new DomainException("El usuario debe ser mayor de edad");
        }
        if (!propietario.getNumeroDocumento().matches(REGEX_DOCUMENTO)) {
            throw new DomainException("El documento debe ser únicamente numérico");
        }
        if (!propietario.getCelular().matches(REGEX_CELULAR)) {
            throw new DomainException("Formato de celular inválido (máx 13 caracteres)");
        }
        if (!propietario.getCorreo().matches(REGEX_CORREO)) {
            throw new DomainException("El correo no tiene un formato válido");
        }
        propietario.setClave(passwordHandler.encode(propietario.getClave()));
        propietario.setRolId(2L);
        usuarioPersistencePort.guardarPropietario(propietario);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioPersistencePort.obtenerUsuarioPorId(id)
                .orElseThrow(() ->
                        new DomainException(
                                "No existe un usuario con el id: " + id
                        )
                );
    }

    @Override
    public void crearEmpleado(Usuario empleado, Long propietarioId) {
        if (empleado.getRestauranteId() == null) {
            throw new DomainException("El empleado debe estar asociado a un restaurante");
        }
        if (!restauranteValidationPort.validarPropietarioRestaurante(empleado.getRestauranteId(), propietarioId)) {
            throw new DomainException("El restaurante no pertenece al propietario autenticado");
        }
        if (!empleado.getNumeroDocumento().matches(REGEX_DOCUMENTO)) {
            throw new DomainException("El documento debe ser únicamente numérico");
        }
        if (!empleado.getCelular().matches(REGEX_CELULAR)) {
            throw new DomainException("Formato de celular inválido (máx 13 caracteres)");
        }
        if (!empleado.getCorreo().matches(REGEX_CORREO)) {
            throw new DomainException("El correo no tiene un formato válido");
        }
        empleado.setClave(passwordHandler.encode(empleado.getClave()));
        empleado.setRolId(3L);
        usuarioPersistencePort.guardarEmpleado(empleado);
    }

    @Override
    public void crearCliente(Usuario cliente) {
        if (!cliente.getNumeroDocumento().matches(REGEX_DOCUMENTO)) {
            throw new DomainException("El documento debe ser únicamente numérico");
        }
        if (!cliente.getCelular().matches(REGEX_CELULAR)) {
            throw new DomainException("Formato de celular inválido (máx 13 caracteres)");
        }
        if (!cliente.getCorreo().matches(REGEX_CORREO)) {
            throw new DomainException("El correo no tiene un formato válido");
        }
        cliente.setClave(passwordHandler.encode(cliente.getClave()));
        cliente.setRolId(4L);
        usuarioPersistencePort.guardarCliente(cliente);
    }


}
