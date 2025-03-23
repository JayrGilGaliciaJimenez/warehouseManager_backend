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
    @Value("${admin.lastname}")
    private String adminLastname;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Value("${user.name}")
    private String userName;
    @Value("${user.lastname}")
    private String userLastname;
    @Value("${user.email}")
    private String userEmail;
    @Value("${user.password}")
    private String userPassword;


    private void createUserIfNotExists(String name, String lastname, String email, String password, RoleModel role) {
        if (userRepository.findByEmail(email) == null) {
            UserModel user = new UserModel();
            user.setName(name);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setStatus("Active");
            userRepository.save(user);
        }
    }

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

        createUserIfNotExists(adminName,adminLastname, adminEmail, adminPassword, adminRole);
        createUserIfNotExists(userName, userLastname, userEmail, userPassword, userRole);

    }
}
