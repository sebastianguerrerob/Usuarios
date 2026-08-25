package com.example.Usuarios.domain.spi;

public interface IRestauranteValidationPort {
    boolean validarPropietarioRestaurante(Long restauranteId, Long propietarioId);
}
