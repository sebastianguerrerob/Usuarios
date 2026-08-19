package com.example.Usuarios.domain.usecase;

import com.example.Usuarios.application.handler.IPasswordHandler;
import com.example.Usuarios.domain.api.IUsuarioServicePort;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordHandler passwordHandler;
    private static final String REGEX_DOCUMENTO = "\\d+";
    private static final String REGEX_CELULAR = "^\\+?\\d{1,12}$";
    private static final String REGEX_CORREO = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public void crearPropietario(Usuario propietario) {
        if (Period.between(propietario.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new RuntimeException("El usuario debe ser mayor de edad");
        }
        if (!propietario.getNumeroDocumento().matches(REGEX_DOCUMENTO)) {
            throw new RuntimeException("El documento debe ser únicamente numérico");
        }
        if (!propietario.getCelular().matches(REGEX_CELULAR)) {
            throw new RuntimeException("Formato de celular inválido (máx 13 caracteres)");
        }
        if (!propietario.getCorreo().matches(REGEX_CORREO)) {
            throw new RuntimeException("El correo no tiene un formato válido");
        }
        propietario.setClave(passwordHandler.encode(propietario.getClave()));
        propietario.setRolId(2L);
        usuarioPersistencePort.guardarPropietario(propietario);
    }



}
