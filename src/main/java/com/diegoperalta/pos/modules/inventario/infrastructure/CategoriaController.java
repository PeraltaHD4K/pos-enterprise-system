package com.diegoperalta.pos.modules.inventario.infrastructure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.diegoperalta.pos.modules.inventario.domain.Categoria;

@RestController
@RequestMapping("/inventario/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaRepository repository;

    @GetMapping
    public List<Categoria> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return repository.save(categoria);
    }
}
