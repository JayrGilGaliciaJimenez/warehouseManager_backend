package utez.edu.mx.warehousemanager_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import utez.edu.mx.warehousemanager_backend.model.RoleModel;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.repository.IRoleRepository;
import utez.edu.mx.warehousemanager_backend.repository.IUserRepository;

import java.time.LocalDateTime;


@Configuration
@RequiredArgsConstructor
public class InitialConfig implements CommandLineRunner {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        RoleModel adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            RoleModel role = new RoleModel();
            role.setName("ROLE_ADMIN");
            role.setCreationDate(LocalDateTime.now());
            return roleRepository.save(role);
        });

        RoleModel userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            RoleModel role = new RoleModel();
            role.setName("ROLE_USER");
            role.setCreationDate(LocalDateTime.now());
            return roleRepository.save(role);
        });



        if (userRepository.findByEmail(adminEmail) == null) {
            UserModel user = new UserModel();
            user.setName(adminName);
            user.setLastname(adminEmail);
            user.setEmail(adminEmail);
            user.setUsername(adminName);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole(adminRole);
            user.setStatus("Active");
            userRepository.save(user);
        }
    }
}
