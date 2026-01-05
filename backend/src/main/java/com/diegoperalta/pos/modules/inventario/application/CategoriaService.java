package com.diegoperalta.pos.modules.inventario.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.inventario.application.dto.CategoriaDTO;
import com.diegoperalta.pos.modules.inventario.domain.Categoria;
import com.diegoperalta.pos.modules.inventario.infrastructure.CategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Categoria obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    public Categoria crearCategoria(CategoriaDTO dto) {
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

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Long id, CategoriaDTO dto) {
        Categoria categoria = obtenerCategoriaPorId(id);

        // Validar duplicados al editar
        if (!categoria.getNombre().equalsIgnoreCase(dto.getNombre())
                && categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + dto.getNombre(),
                    HttpStatus.CONFLICT);
        }

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);

        // Soft Delete
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }
}
