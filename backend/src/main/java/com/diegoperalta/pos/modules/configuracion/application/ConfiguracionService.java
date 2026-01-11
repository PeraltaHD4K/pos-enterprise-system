package com.diegoperalta.pos.modules.configuracion.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diegoperalta.pos.modules.configuracion.domain.Configuracion;
import com.diegoperalta.pos.modules.configuracion.infrastructure.ConfiguracionRepository;

@Service
public class ConfiguracionService {
    @Autowired
    private ConfiguracionRepository repository;

    public Configuracion getConfiguracion() {
        return repository.findById("configuracion").orElse(null);
    }

    public Map<String, String> obtenerConfiguracionCompleta() {
        List<Configuracion> lista = repository.findAll();
        Map<String, String> mapa = new HashMap<>();

        // Valores por defecto por si la BD está vacía
        mapa.put("NOMBRE_TIENDA", "Mi Punto de Venta");
        mapa.put("RFC", "XAXX010101000");
        mapa.put("DIRECCION", "Conocido");
        mapa.put("TICKET_FOOTER", "Gracias por su compra");

        // Sobreescribir con lo que venga de BD
        for (Configuracion conf : lista) {
            mapa.put(conf.getClave(), conf.getValor());
        }
        return mapa;
    }

    // Guardar cambios masivos (Recibe un Map desde el Frontend)
    public void actualizarConfiguracion(Map<String, String> nuevosValores) {
        List<Configuracion> entidades = nuevosValores.entrySet().stream()
                .map(entry -> new Configuracion(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        repository.saveAll(entidades);
    }
}
