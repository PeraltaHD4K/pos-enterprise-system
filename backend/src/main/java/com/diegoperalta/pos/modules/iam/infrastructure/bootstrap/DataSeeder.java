package com.diegoperalta.pos.modules.iam.infrastructure.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.diegoperalta.pos.modules.clientes.domain.Cliente;
import com.diegoperalta.pos.modules.clientes.infrastructure.ClienteRepository;
import com.diegoperalta.pos.modules.iam.domain.Rol;
import com.diegoperalta.pos.modules.iam.domain.Usuario;
import com.diegoperalta.pos.modules.iam.infrastructure.RolRepository;
import com.diegoperalta.pos.modules.iam.infrastructure.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClienteRepository clienteRepository;

    @Value("${app.security.admin-password:admin123}")
    private String adminPassword;

    @Value("${app.security.admin-username:admin}")
    private String adminUsername;

    @Value("${app.security.cajero-password:cajero123}")
    private String cajeroPassword;

    @Override
    public void run(String... args) throws Exception {
        cargarRoles();
        cargarUsuarios();
        cargarClienteGeneral();
    }

    private void cargarRoles() {
        crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("GERENTE");
        crearRolSiNoExiste("CAJERO");
    }

    private void crearRolSiNoExiste(String nombre) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rolRepository.save(rol);
        }
    }

    private void cargarUsuarios() {
        // 1. Lógica para el SUPER ADMIN
        if (usuarioRepository.findByUsername(adminUsername).isEmpty()) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado"));

            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword)); // 🔒 Encriptado
            admin.setRol(rolAdmin);
            admin.setNombreCompleto("Administrador Sistema");
            admin.setActivo(true);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: " + adminUsername);
        } else {
            System.out.println("ℹ️ El usuario ADMIN ya existe.");
        }

        // 2. Lógica para el CAJERO DE PRUEBA (Dev)
        String cajeroUsername = "cajero";
        if (usuarioRepository.findByUsername(cajeroUsername).isEmpty()) {
            Rol rolCajero = rolRepository.findByNombre("CAJERO")
                    .orElseThrow(() -> new RuntimeException("Error: Rol CAJERO no encontrado"));

            Usuario cajero = new Usuario();
            cajero.setUsername(cajeroUsername);
            cajero.setPasswordHash(passwordEncoder.encode(cajeroPassword)); // Password fija para desarrollo
            cajero.setRol(rolCajero);
            cajero.setNombreCompleto("Cajero Principal");
            cajero.setActivo(true);

            usuarioRepository.save(cajero);
            System.out.println("✅ Usuario CAJERO creado: " + cajeroUsername);
        }
    }

    private void cargarClienteGeneral() {
        if (clienteRepository.count() == 0) {
            Cliente publico = new Cliente();
            publico.setNombre("Público en General");
            publico.setTelefono("0000000000");
            publico.setEmail("publico@mitienda.com");
            publico.setPuntosFidelidad(0);
            clienteRepository.save(publico);
        }
    }
}
