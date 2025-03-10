package utez.edu.mx.warehousemanager_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import utez.edu.mx.warehousemanager_backend.jwt.AuthRequest;
import utez.edu.mx.warehousemanager_backend.jwt.AuthResponse;
import utez.edu.mx.warehousemanager_backend.jwt.JwtTokenUtil;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.service.UserService;

@RestController
@Slf4j
@RequestMapping("/api")
public class AccessController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    AccessController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
            UserService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = usuarioService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            log.warn("{}", authentication);

            UserModel user = this.userService.findByEmail(request.getEmail());
            String accessToken = this.jwtTokenUtil.generatedToken(user);
            String role = user.getRole().getName();
            Integer id = user.getId();
            AuthResponse response = new AuthResponse(request.getEmail(), accessToken, role, id);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", request.getEmail());

            UserModel user = this.userService.findByEmail(request.getEmail());
            if (user == null) {
                log.warn("User with email {} not found", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("No user registered with this email", null, null, null));
            }

            if (user.getStatus().getName().equals("Inactive")) {
                log.warn("User {} is inactive", request.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthResponse("User is inactive", null, null, null));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid credentials", null, null, null));
        }
    }
}
