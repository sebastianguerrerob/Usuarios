package com.example.Usuarios.domain.util;

public final class DomainConstants {

    private DomainConstants() {}

    // Regex
    public static final String REGEX_DOCUMENTO = "\\d+";
    public static final String REGEX_CELULAR = "^\\+?\\d{1,12}$";
    public static final String REGEX_CORREO = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Mensajes de error
    public static final String USUARIO_MAYOR_DE_EDAD = "El usuario debe ser mayor de edad";
    public static final String DOCUMENTO_NUMERICO = "El documento debe ser únicamente numérico";
    public static final String CELULAR_INVALIDO = "Formato de celular inválido (máx 13 caracteres)";
    public static final String CORREO_INVALIDO = "El correo no tiene un formato válido";
    public static final String USUARIO_NO_ENCONTRADO = "No existe un usuario con el id: ";
    public static final String EMPLEADO_SIN_RESTAURANTE = "El empleado debe estar asociado a un restaurante";
    public static final String RESTAURANTE_NO_PERTENECE = "El restaurante no pertenece al propietario autenticado";
    public static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";
}
