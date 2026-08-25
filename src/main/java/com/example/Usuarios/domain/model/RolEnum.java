package com.example.Usuarios.domain.model;

public enum RolEnum {
    ADMIN(1L),
    PROPIETARIO(2L),
    EMPLEADO(3L),
    CLIENTE(4L);

    private final Long id;

    RolEnum(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
