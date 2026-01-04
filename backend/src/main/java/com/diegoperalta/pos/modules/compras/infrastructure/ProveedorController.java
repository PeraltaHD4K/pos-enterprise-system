package com.diegoperalta.pos.modules.compras.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.compras.application.dto.ProveedorDTO;
import com.diegoperalta.pos.modules.compras.domain.Proveedor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proveedores")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class ProveedorController {

    @Autowired
    private ProveedorRepository repository;

    @GetMapping
    public List<Proveedor> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Proveedor crear(@Valid @RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setEmpresa(dto.getEmpresa());
        proveedor.setContacto(dto.getContacto());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDiaVisita(dto.getDiaVisita());

        return repository.save(proveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorDTO dto) {
        return repository.findById(id)
                .map(proveedor -> {
                    proveedor.setEmpresa(dto.getEmpresa());
                    proveedor.setContacto(dto.getContacto());
                    proveedor.setTelefono(dto.getTelefono());
                    proveedor.setEmail(dto.getEmail());
                    proveedor.setDiaVisita(dto.getDiaVisita());
                    return ResponseEntity.ok(repository.save(proveedor));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
