package com.diegoperalta.pos.modules.compras.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.compras.application.dto.ProveedorDTO;
import com.diegoperalta.pos.modules.compras.domain.Proveedor;
import com.diegoperalta.pos.modules.compras.infrastructure.ProveedorRepository;

@Service
public class ProveedorService {
    @Autowired
    private ProveedorRepository repository;

    public List<Proveedor> listarTodos() {
        // Usamos el método mágico para no traer los borrados
        return repository.findByActivoTrue();
    }

    public Proveedor obtenerPorId(Long id) {
        return repository.findById(id)
                .filter(Proveedor::isActivo) // Asegura que no traiga borrados (usa isActivo por ser primitivo boolean)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional
    public Proveedor crear(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        mapDtoToEntity(dto, proveedor);
        proveedor.setActivo(true); // Por defecto
        return repository.save(proveedor);
    }

    @Transactional
    public Proveedor actualizar(Long id, ProveedorDTO dto) {
        Proveedor proveedor = obtenerPorId(id); // Reusa la lógica de búsqueda segura
        mapDtoToEntity(dto, proveedor);
        return repository.save(proveedor);
    }

    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = obtenerPorId(id);
        proveedor.setActivo(false); // Soft Delete
        repository.save(proveedor);
    }

    // Método auxiliar para no repetir código de mapeo
    private void mapDtoToEntity(ProveedorDTO dto, Proveedor proveedor) {
        proveedor.setEmpresa(dto.getEmpresa());
        proveedor.setContacto(dto.getContacto());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDiaVisita(dto.getDiaVisita());
    }
}
