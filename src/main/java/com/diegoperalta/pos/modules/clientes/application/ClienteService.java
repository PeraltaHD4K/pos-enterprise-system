package com.diegoperalta.pos.modules.clientes.application;

import java.util.List;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.clientes.application.dto.ClienteDTO;
import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.clientes.infrastructure.ClienteRepository;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public List<Cliente> buscarClientes(String query) {
        return clienteRepository.buscarClientes(query);
    }

    public Cliente crearCliente(ClienteDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un cliente con ese email", HttpStatus.BAD_REQUEST);
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setPuntosFidelidad(0); // Siempre nace en 0

        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Long id, ClienteDTO dto) {
        Cliente cliente = buscarPorId(id);

        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        // NO tocamos puntosFidelidad aquí

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }
}
