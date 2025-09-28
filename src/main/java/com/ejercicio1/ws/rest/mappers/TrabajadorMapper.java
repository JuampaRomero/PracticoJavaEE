package com.ejercicio1.ws.rest.mappers;

import com.ejercicio1.entities.TrabajadorSalud;
import com.ejercicio1.ws.rest.dto.TrabajadorDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre TrabajadorSalud (Entity) y TrabajadorDTO
 */
public class TrabajadorMapper {
    
    /**
     * Convierte de Entity a DTO
     */
    public static TrabajadorDTO toDTO(TrabajadorSalud entity) {
        if (entity == null) {
            return null;
        }
        
        return new TrabajadorDTO(
            entity.getCedula(),
            entity.getNombre(),
            entity.getApellido(),
            entity.getEspecialidad(),
            entity.getMatriculaProfesional(),
            entity.getFechaIngreso(),
            entity.isActivo()
        );
    }
    
    /**
     * Convierte de DTO a Entity
     */
    public static TrabajadorSalud toEntity(TrabajadorDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new TrabajadorSalud(
            dto.getCedula(),
            dto.getNombre(),
            dto.getApellido(),
            dto.getEspecialidad(),
            dto.getMatriculaProfesional(),
            dto.getFechaIngreso(),
            dto.isActivo()
        );
    }
    
    /**
     * Convierte una lista de Entities a DTOs
     */
    public static List<TrabajadorDTO> toDTOList(List<TrabajadorSalud> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(TrabajadorMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza una entity existente con los datos de un DTO
     */
    public static void updateEntityFromDTO(TrabajadorSalud entity, TrabajadorDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setMatriculaProfesional(dto.getMatriculaProfesional());
        entity.setFechaIngreso(dto.getFechaIngreso());
        entity.setActivo(dto.isActivo());
        // No actualizamos la cédula ya que es el ID
    }
}