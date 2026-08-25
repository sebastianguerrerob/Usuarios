package com.example.Usuarios.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoRequestDto {
    private String nombre;
    private String apellido;
    private String numeroDocumento;
    private String celular;
    private String correo;
    private String clave;
    private Long restauranteId;
}
