package utez.edu.mx.warehousemanager_backend.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.service.UserService;

@Component
@Slf4j
public class UserLogin implements UserDetailsService {

    private UserService userService;

    UserLogin(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        UserModel user = this.userService.findByEmail(username);
        if (user == null) {
            log.warn("Username {} not found in the system", username);
            throw new UsernameNotFoundException("Username " + username + " no existe en el sistema");
        }

        if (user.getStatus().getName().equals("Inactive")) {
            log.warn("Username {} is inactive", username);
            throw new UsernameNotFoundException("Username " + username + " está deshabilitado");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        log.info("User {} found with roles: {}", username, user.getRole().getName());
        return new User(user.getEmail(), user.getPassword(), true, true, true, true, authorities);
    }

}
