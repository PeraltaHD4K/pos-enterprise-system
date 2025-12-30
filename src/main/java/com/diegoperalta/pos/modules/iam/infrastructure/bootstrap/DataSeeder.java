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
        // Solo creamos al admin si NO existe ningún usuario en la BD
        if (usuarioRepository.count() == 0) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN").orElseThrow();

            Usuario admin = new Usuario();
            admin.setUsername(adminUsername); // Usamos la variable inyectada

            // 🔒 Aquí está la magia: Usamos la variable, no un texto fijo
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));

            admin.setRol(rolAdmin);
            admin.setNombreCompleto("Administrador Sistema");
            admin.setActivo(true);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN inicial creado.");
            System.out.println(
                    "⚠️ ATENCIÓN: Si es producción, asegúrese de haber cambiado la contraseña por variable de entorno.");
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
