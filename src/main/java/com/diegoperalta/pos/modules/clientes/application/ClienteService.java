package com.diegoperalta.pos.modules.clientes.application;

import java.util.List;

import com.diegoperalta.pos.common.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.clientes.infrastructure.ClienteRepository;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente crearCliente(Cliente cliente) {
        if (cliente.getPuntosFidelidad() == null) {
            cliente.setPuntosFidelidad(0);
        }
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }
}
