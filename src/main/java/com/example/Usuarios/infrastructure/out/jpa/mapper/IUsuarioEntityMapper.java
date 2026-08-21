package com.example.Usuarios.infrastructure.out.jpa.mapper;

import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.infrastructure.out.jpa.entity.RolEntity;
import com.example.Usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioEntityMapper {

    @Mapping(target = "rol", source = "rolId", qualifiedByName = "rolIdToRolEntity")
    UsuarioEntity toEntity(Usuario usuario);

    @Mapping(target = "rolId", source = "rol.id")
    Usuario toUsuario(UsuarioEntity usuarioEntity);

    @Named("rolIdToRolEntity")
    default RolEntity rolIdToRolEntity(Long rolId) {
        if (rolId == null) return null;
        RolEntity rolEntity = new RolEntity();
        rolEntity.setId(rolId);
        return rolEntity;
    }
}