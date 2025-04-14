package utez.edu.mx.warehousemanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utez.edu.mx.warehousemanager.jwt.AuthRequest;
import utez.edu.mx.warehousemanager.jwt.AuthResponse;
import utez.edu.mx.warehousemanager.jwt.JwtTokenUtil;
import utez.edu.mx.warehousemanager.model.AccessLog;
import utez.edu.mx.warehousemanager.model.UserModel;
import utez.edu.mx.warehousemanager.repository.AccessLogRepository;
import utez.edu.mx.warehousemanager.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api")
public class AccessController {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final AccessLogRepository accessLogRepository;

    public AccessController(JwtTokenUtil jwtTokenUtil, UserService userService, AccessLogRepository accessLogRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.accessLogRepository = accessLogRepository;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            UserModel user = this.userService.findByEmail(request.getEmail());
            if (user == null) {
                log.warn("User with email {} not found", request.getEmail());
                accessLogRepository.save(new AccessLog(
                        null,
                        request.getEmail(),
                        LocalDateTime.now(),
                        false,
                        ipAddress,
                        userAgent
                ));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("No user registered with this email", null, null, null));
            }

            if (user.getStatus().equals("Inactive")) {
                log.warn("User {} is inactive", request.getEmail());
                accessLogRepository.save(new AccessLog(
                        null,
                        request.getEmail(),
                        LocalDateTime.now(),
                        false,
                        ipAddress,
                        userAgent
                ));
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthResponse("User is inactive", null, null, null));
            }

            if (user.getStatus().equals("Pending")) {
                log.warn("User {} has status Pending", request.getEmail());
                accessLogRepository.save(new AccessLog(
                        null,
                        request.getEmail(),
                        LocalDateTime.now(),
                        false,
                        ipAddress,
                        userAgent
                ));
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthResponse("User must update password", null, null, null));
            }
            String accessToken = this.jwtTokenUtil.generatedToken(user);
            String role = user.getRole().getName();
            UUID uuid = user.getUuid();
            AuthResponse response = new AuthResponse(request.getEmail(), accessToken, role, uuid);

            accessLogRepository.save(new AccessLog(
                    null,
                    request.getEmail(),
                    LocalDateTime.now(),
                    true,
                    ipAddress,
                    userAgent
            ));

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", request.getEmail());
            accessLogRepository.save(new AccessLog(
                    null,
                    request.getEmail(),
                    LocalDateTime.now(),
                    false,
                    ipAddress,
                    userAgent
            ));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid credentials", null, null, null));
        } catch (Exception e) {
            log.error("Error occurred during login for user: {}", request.getEmail(), e);
            accessLogRepository.save(new AccessLog(
                    null,
                    request.getEmail(),
                    LocalDateTime.now(),
                    false,
                    ipAddress,
                    userAgent
            ));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("An error occurred", null, null, null));
        }
    }

}