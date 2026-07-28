package com.diegoperalta.pos.modules.compra.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegoperalta.pos.common.exception.ResourceNotFoundException;
import com.diegoperalta.pos.modules.compra.application.dto.ProveedorDTO;
import com.diegoperalta.pos.modules.compra.application.dto.ProveedorResponseDTO;
import com.diegoperalta.pos.modules.compra.domain.Proveedor;
import com.diegoperalta.pos.modules.compra.infrastructure.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    
    private final ProveedorRepository repository;

    public List<ProveedorResponseDTO> listarTodos() {
        return repository.findByActivoTrue().stream().map(this::mapToDTO).toList();
    }

    public ProveedorResponseDTO obtenerPorId(UUID id) {
        Proveedor proveedor = obtenerEntidadPorId(id);
        return mapToDTO(proveedor);
    }

    private Proveedor obtenerEntidadPorId(UUID id) {
        return repository.findById(id)
                .filter(Proveedor::isActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional
    public ProveedorResponseDTO crear(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        mapDtoToEntity(dto, proveedor);
        proveedor.setActivo(true); // Por defecto
        return mapToDTO(repository.save(proveedor));
    }

    @Transactional
    public ProveedorResponseDTO actualizar(UUID id, ProveedorDTO dto) {
        Proveedor proveedor = obtenerEntidadPorId(id); // Reusa la lógica de búsqueda segura
        mapDtoToEntity(dto, proveedor);
        return mapToDTO(repository.save(proveedor));
    }

    @Transactional
    public void eliminar(UUID id) {
        Proveedor proveedor = obtenerEntidadPorId(id);
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

    private ProveedorResponseDTO mapToDTO(Proveedor proveedor) {
        return new ProveedorResponseDTO(
            proveedor.getId(),
            proveedor.getEmpresa(),
            proveedor.getContacto(),
            proveedor.getTelefono(),
            proveedor.getEmail(),
            proveedor.getDiaVisita(),
            proveedor.isActivo()
        );
    }
}
