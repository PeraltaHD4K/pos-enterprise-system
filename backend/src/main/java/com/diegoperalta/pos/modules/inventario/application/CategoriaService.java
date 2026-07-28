package com.diegoperalta.pos.modules.inventario.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.inventario.application.dto.CategoriaDTO;
import com.diegoperalta.pos.modules.inventario.application.dto.CategoriaResponseDTO;
import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findByActivoTrue().stream().map(this::mapToDTO).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(UUID id) {
        Categoria categoria = obtenerEntidadCategoriaPorId(id);
        return mapToDTO(categoria);
    }

    @Transactional(readOnly = true)
    private Categoria obtenerEntidadCategoriaPorId(UUID id) {
        return categoriaRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaDTO dto) {
        // 1. Validar nombre único
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + dto.getNombre(),
                    HttpStatus.CONFLICT);
        }

        // 2. Mapeo Manual
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setActivo(true);

        return mapToDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO actualizarCategoria(UUID id, CategoriaDTO dto) {
        Categoria categoria = obtenerEntidadCategoriaPorId(id);

        // Validar duplicados al editar
        if (!categoria.getNombre().equalsIgnoreCase(dto.getNombre())
                && categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + dto.getNombre(),
                    HttpStatus.CONFLICT);
        }

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        return mapToDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminarCategoria(UUID id) {
        Categoria categoria = obtenerEntidadCategoriaPorId(id);

        // Soft Delete
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaResponseDTO mapToDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
            categoria.getId(),
            categoria.getNombre(),
            categoria.getDescripcion(),
            categoria.getActivo() != null ? categoria.getActivo() : false
        );
    }
}
