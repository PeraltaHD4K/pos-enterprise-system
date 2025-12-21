package com.diegoperalta.pos.modules.compras.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.compras.domain.Proveedor;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorRepository repository;

    @GetMapping
    public List<Proveedor> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Proveedor crear(@RequestBody Proveedor proveedor) {
        return repository.save(proveedor);
    }
}