package com.example.Usuarios.infrastructure.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "plazoleta", url = "${plazoleta.base-url}")
public interface IPlazoletaFeignClient {

    @GetMapping("/restaurante/{idRestaurante}/propietario/{idPropietario}/validar")
    Map<String, Object> validarPropietarioRestaurante(
            @PathVariable Long idRestaurante,
            @PathVariable Long idPropietario
    );
}
