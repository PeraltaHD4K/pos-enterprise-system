package com.diegoperalta.pos.modules.cliente.application;

import java.util.List;

import com.diegoperalta.pos.common.exception.BusinessException;
import com.diegoperalta.pos.common.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.cliente.application.dto.ClienteDTO;
import com.diegoperalta.pos.modules.cliente.application.dto.ClienteResponseDTO;
import com.diegoperalta.pos.modules.cliente.domain.Cliente;
import com.diegoperalta.pos.modules.cliente.infrastructure.ClienteRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public List<ClienteResponseDTO> buscarClientes(String query) {
        return clienteRepository.buscarClientes(query).stream().map(this::mapToDTO).toList();
    }

    public ClienteResponseDTO crearCliente(ClienteDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un cliente con ese email", HttpStatus.BAD_REQUEST);
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setPuntosFidelidad(0); // Siempre nace en 0

        return mapToDTO(clienteRepository.save(cliente));
    }

    public ClienteResponseDTO actualizarCliente(UUID id, ClienteDTO dto) {
        Cliente cliente = buscarPorId(id);

        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        // NO tocamos puntosFidelidad aquí

        return mapToDTO(clienteRepository.save(cliente));
    }

    public Cliente buscarPorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private ClienteResponseDTO mapToDTO(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getTelefono(),
            cliente.getEmail(),
            cliente.getPuntosFidelidad()
        );
    }
}
