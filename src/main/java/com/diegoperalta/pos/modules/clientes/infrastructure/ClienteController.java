package com.diegoperalta.pos.modules.clientes.infrastructure;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.clientes.application.ClienteService;
import com.diegoperalta.pos.modules.clientes.application.dto.ClienteDTO;
import com.diegoperalta.pos.modules.clientes.domain.Cliente;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscar(@RequestParam String query) {
        return ResponseEntity.ok(clienteService.buscarClientes(query));
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.crearCliente(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }
}
