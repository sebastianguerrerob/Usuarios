package com.example.Usuarios.infrastructure.out.feign;

import com.example.Usuarios.domain.spi.IRestauranteValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RestauranteValidationFeignAdapter implements IRestauranteValidationPort {

    private final IPlazoletaFeignClient plazoletaFeignClient;

    @Override
    public boolean validarPropietarioRestaurante(Long restauranteId, Long propietarioId) {
        try {
            Map<String, Object> response = plazoletaFeignClient.validarPropietarioRestaurante(restauranteId, propietarioId);
            return response != null && Boolean.TRUE.equals(response.get("esPropietario"));
        } catch (Exception e) {
            return false;
        }
    }
}
