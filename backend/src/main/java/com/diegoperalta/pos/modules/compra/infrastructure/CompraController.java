package com.diegoperalta.pos.modules.compra.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diegoperalta.pos.modules.compra.application.CompraService;
import com.diegoperalta.pos.modules.compra.application.dto.CompraRegistroDTO;
import com.diegoperalta.pos.modules.compra.application.dto.CompraResponseDTO;
import com.diegoperalta.pos.modules.compra.domain.Compra;
import com.diegoperalta.pos.modules.compra.infrastructure.CompraRepository;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/compras")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
@RequiredArgsConstructor
public class CompraController {
    
    private final CompraService compraService;

    
    private final CompraRepository compraRepository;

    @PostMapping
    public ResponseEntity<CompraResponseDTO> registrarCompra(@Valid @RequestBody CompraRegistroDTO dto) {
        Compra nuevaCompra = compraService.registrarCompra(dto);
        return ResponseEntity.ok(mapToDTO(nuevaCompra));
    }

    @PostMapping("/confirmar/{id}")
    public ResponseEntity<CompraResponseDTO> confirmarRecepcion(@PathVariable Long id) {
        Compra compraConfirmada = compraService.confirmarRecepcion(id);
        return ResponseEntity.ok(mapToDTO(compraConfirmada));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarCompras() {
        return ResponseEntity.ok(compraRepository.findAllConDatos().stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return compraRepository.findByIdConDetalles(id)
                .map(this::mapToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private CompraResponseDTO mapToDTO(Compra c) {
        CompraResponseDTO dto = new CompraResponseDTO();
        dto.setId(c.getId());
        dto.setFolioFactura(c.getFolioFactura());
        if (c.getProveedor() != null) {
            dto.setNombreProveedor(c.getProveedor().getEmpresa());
        }
        if (c.getUsuario() != null) {
            dto.setNombreUsuario(c.getUsuario().getUsername());
        }
        dto.setFechaPedido(c.getFechaPedido());
        dto.setFechaRecepcion(c.getFechaRecepcion());
        dto.setFechaEstimadaEntrega(c.getFechaEstimadaEntrega());
        dto.setEstado(c.getEstado());
        dto.setTotal(c.getTotal());
        dto.setObservaciones(c.getObservaciones());
        return dto;
    }
}
