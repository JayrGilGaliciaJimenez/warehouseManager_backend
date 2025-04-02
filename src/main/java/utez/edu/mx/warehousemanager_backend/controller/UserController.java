package utez.edu.mx.warehousemanager_backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager_backend.model.EmailModel;
import utez.edu.mx.warehousemanager_backend.model.ResetTokenModel;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.repository.IPasswordResetToken;
import utez.edu.mx.warehousemanager_backend.service.EmailService;
import utez.edu.mx.warehousemanager_backend.service.UserService;
import utez.edu.mx.warehousemanager_backend.utils.Utilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final IPasswordResetToken passwordRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String RECORD_NOT_FOUND = "Record not found.";
    private static final String INTERNAL_SERVER_ERROR = "An internal server error occurred.";
    private static final String LOG_RECORD_NOT_FOUND = "User not found with UUID: {}";

    UserController(UserService userService, EmailService emailService, BCryptPasswordEncoder passwordEncoder,
            IPasswordResetToken passwordRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordRepository = passwordRepository;
    }

    // GetAll
    @GetMapping("/user/list")
    public List<UserModel> users() {
        log.info("Fetching all users");
        return this.userService.getAll();
    }

    // GetByUUID
    @GetMapping("/user/{uuid}")
    public UserModel getByUuid(@PathVariable("uuid") UUID uuid) {
        log.info("Fetching user with UUID: {}", uuid);
        return this.userService.findByUuid(uuid);
    }

    // Register User
    @PostMapping("/user/register")
    public ResponseEntity<Object> createUser(@RequestBody UserModel request) {
        try {
            log.info("Attempting to register user with email: {}", request.getEmail());
            UserModel user = userService.findByEmail(request.getEmail());
            if (user != null) {
                log.warn("Email already registered: {}", request.getEmail());
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, "Email already registered");
            }
            log.info("Encoding password for user with email: {}", request.getEmail());
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            String activationToken = UUID.randomUUID().toString();
            log.info("Generated activation token for user with email: {}", request.getEmail());
            userService.saveActivationToken(user, activationToken);
            request.setStatus("Pending");

            log.info("Saving user with email: {}", request.getEmail());
            this.userService.save(request);
            log.info("User registered successfully with email: {}", request.getEmail());

            String activationLink = "http://localhost:5173/active-account/" + activationToken;
            log.info("Generated activation link for user with email: {}", request.getEmail());
            EmailModel emailModel = new EmailModel();
            emailModel.setRecipient(request.getEmail());
            emailModel.setSubject("Confirmación de Registro - Activa tu cuenta en las próximas 24 horas");
            emailModel.setMessage(activationLink);
            log.info("Sending registration email to: {}", request.getEmail());
            emailService.sendEmail(emailModel, "activate_account");

            return Utilities.generateResponse(HttpStatus.OK, "Record created succesfully");
        } catch (Exception e) {
            log.error("Error occurred while registering user", e);
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    // Deactivate User
    @PutMapping("/user/deactivate/{uuid}")
    public ResponseEntity<Object> deactivateUser(@PathVariable("uuid") UUID uuid) {
        try {
            log.info("Attempting to deactivate user with UUID: {}", uuid);
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                log.warn(LOG_RECORD_NOT_FOUND, uuid);
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            } else {
                user.setStatus("Inactive");
                this.userService.save(user);
                log.info("User deactivated successfully with UUID: {}", uuid);
                return Utilities.generateResponse(HttpStatus.OK, "User deactivated successfully");
            }
        } catch (Exception e) {
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    // Activate User
    @PutMapping("/user/activate/{uuid}")
    public ResponseEntity<Object> activateUser(@PathVariable("uuid") UUID uuid) {
        try {
            log.info("Attempting to activate user with UUID: {}", uuid);
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                log.warn(LOG_RECORD_NOT_FOUND, uuid);
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            } else {
                user.setStatus("Active");
                this.userService.save(user);
                log.info("User activated successfully with UUID: {}", uuid);
                return Utilities.generateResponse(HttpStatus.OK, "User activated successfully");
            }
        } catch (Exception e) {
            log.error("Error occurred while activating user with UUID: {}", uuid, e);
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    // Delete User
    @DeleteMapping("/user/{uuid}")
    public ResponseEntity<Object> deleteUser(@PathVariable("uuid") UUID uuid) {
        try {
            log.info("Attempting to delete user with UUID: {}", uuid);
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                log.warn(LOG_RECORD_NOT_FOUND, uuid);
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            } else {
                this.userService.delete(uuid);
                log.info("User deleted successfully with UUID: {}", uuid);
                return Utilities.generateResponse(HttpStatus.OK, "Record deleted succesfully");
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting user with UUID: {}", uuid, e);
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    // Reset Password Send Email
    @PostMapping("/auth/reset-email")
    public ResponseEntity<Object> resetPasswordEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            log.info("Attempting to reset password for email: {}", email);
            UserModel user = userService.findByEmail(email);
            if (user == null) {
                log.warn("User not found with email: {}", email);
                return Utilities.generateResponse(HttpStatus.NOT_FOUND, RECORD_NOT_FOUND);
            }

            if ("Pending".equals(user.getStatus())) {
                log.warn("User with email {} has status Pending and must activate their account first", email);
                return Utilities.generateResponse(HttpStatus.UNAUTHORIZED, "User must activate their account first");
            }

            ResetTokenModel existingToken = passwordRepository.findByUserAndExpiryDateAfter(user, LocalDateTime.now());
            if (existingToken != null) {
                log.warn("Active password reset token already exists for email: {}", email);
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, "Active password reset token already exists");
            }

            String token = UUID.randomUUID().toString();
            userService.savePasswordResetToken(user, token);

            String resetLink = "http://localhost:5173/reset-password/" + token;
            log.info("Generated reset password link for user with email: {}", email);
            EmailModel emailModel = new EmailModel();
            emailModel.setRecipient(email);
            emailModel.setSubject("Recuperación de Contraseña - Recupera tu contraseña en las próximas 24 horas");
            emailModel.setMessage(resetLink);
            log.info("Sending registration email to: {}", email);
            emailService.sendEmail(emailModel, "reset_password");
            log.info("Password reset token generated for email: {}", email);
            return new ResponseEntity<>(Utilities.generateResponse(HttpStatus.OK, "Token: " + token), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while resetting password for email: {}", email, e);
            return new ResponseEntity<>(Utilities.generateResponse(HttpStatus.BAD_REQUEST, INTERNAL_SERVER_ERROR),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Reset User Password
    @PostMapping("/auth/reset-password/{token}")
    public ResponseEntity<Object> resetPassword(@PathVariable String token, @RequestBody Map<String, String> request) {
        try {
            log.info("Attempting to reset password with token: {}", token);
            String newPassword = request.get("password");
            ResetTokenModel resetToken = passwordRepository.findByToken(token);
            if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                log.warn("Invalid or expired token: {}", token);
                return new ResponseEntity<>(Utilities.generateResponse(HttpStatus.BAD_REQUEST, INTERNAL_SERVER_ERROR),
                        HttpStatus.BAD_REQUEST);
            }
            UserModel user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            passwordRepository.delete(resetToken);
            log.info("Password reset successfully for user with email: {}", user.getEmail());
            return new ResponseEntity<>(Utilities.generateResponse(HttpStatus.OK, "Password reset successfully"),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while resetting password with token: {}", token, e);
            return new ResponseEntity<>(
                    Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
