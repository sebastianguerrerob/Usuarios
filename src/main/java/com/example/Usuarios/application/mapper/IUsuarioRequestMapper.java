package com.example.Usuarios.application.mapper;

import com.example.Usuarios.application.dto.UsuarioRequestDto;
import com.example.Usuarios.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rolId", ignore = true)
    Usuario toUsuario(UsuarioRequestDto usuarioRequestDto);
}