package com.example.Usuarios.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    @Column(unique = true, nullable = false)
    private String numeroDocumento;
    private String celular;
    private LocalDate fechaNacimiento;
    @Column(unique = true, nullable = false)
    private String correo;
    private String clave;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;

    @Column(name = "id_restaurante")
    private Long restauranteId;
}