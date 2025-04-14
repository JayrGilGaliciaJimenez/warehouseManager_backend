package utez.edu.mx.warehousemanager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import utez.edu.mx.warehousemanager.dto.UserDto;
import utez.edu.mx.warehousemanager.mapper.UserMapper;
import utez.edu.mx.warehousemanager.model.EmailModel;
import utez.edu.mx.warehousemanager.model.ResetTokenModel;
import utez.edu.mx.warehousemanager.model.RoleModel;
import utez.edu.mx.warehousemanager.model.UserModel;
import utez.edu.mx.warehousemanager.repository.IPasswordResetToken;
import utez.edu.mx.warehousemanager.service.EmailService;
import utez.edu.mx.warehousemanager.service.UserService;
import utez.edu.mx.warehousemanager.utils.Utilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
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
    public List<UserDto> listUsers() {
        return userService.getAllUsersDto();
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
            UserModel existingUser = userService.findByEmail(request.getEmail());
            if (existingUser != null) {
                log.warn("Email already registered: {}", request.getEmail());
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, "Email already registered");
            }

            String temporaryPassword = UUID.randomUUID().toString();
            log.info("Generated temporary password for user with email: {} (password not logged for security reasons)",
                    request.getEmail());
            request.setPassword(passwordEncoder.encode(temporaryPassword));
            request.setStatus("Pending");

            log.info("Saving user with email: {}", request.getEmail());
            this.userService.save(request);

            String activationToken = UUID.randomUUID().toString();
            log.info("Generated activation token for user with email: {}", request.getEmail());
            userService.saveActivationToken(request, activationToken);

            String activationLink = "http://localhost:5173/active-account/" + activationToken;
            log.info("Generated activation link for user with email: {}", request.getEmail());
            EmailModel emailModel = new EmailModel();
            emailModel.setRecipient(request.getEmail());
            emailModel.setSubject("Confirmación de Registro - Activa tu cuenta en las próximas 24 horas");
            emailModel.setMessage(activationLink);
            log.info("Sending registration email to: {}", request.getEmail());
            emailService.sendEmail(emailModel, "activate_account");

            log.info("User registered successfully with email: {}", request.getEmail());
            return Utilities.generateResponse(HttpStatus.OK, "Record created successfully");
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

    // Update User
    @PutMapping("/user/update/{uuid}")
    public ResponseEntity<Object> updateUser(@PathVariable("uuid") UUID uuid, @RequestBody UserDto request) {
        try {
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                log.warn(LOG_RECORD_NOT_FOUND, uuid);
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            }

            if (request.getName() != null && !request.getName().isEmpty()) {
                user.setName(request.getName());
            }
            if (request.getLastname() != null && !request.getLastname().isEmpty()) {
                user.setLastname(request.getLastname());
            }
            if (request.getRole() != null) {
                RoleModel roleModel = UserMapper.toRoleModel(request.getRole());
                user.setRole(roleModel);
            }

            this.userService.save(user);
            log.info("User updated successfully with UUID: {}", uuid);
            return Utilities.generateResponse(HttpStatus.OK, "User updated successfully");
        } catch (EntityNotFoundException e) {
            log.error(LOG_RECORD_NOT_FOUND, uuid, e);
            return Utilities.generateResponse(HttpStatus.NOT_FOUND, RECORD_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error occurred while updating user with UUID: {}", uuid, e);
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

    // Activate Account
    @PostMapping("/auth/activate-account/{token}")
    public ResponseEntity<Object> activateAccount(@PathVariable String token,
            @RequestBody Map<String, String> request) {
        log.info("Received request to activate account with token: {}", token);
        try {
            log.info("Extracting password from request body for token: {}", token);
            String newPassword = request.get("password");

            log.info("Searching for reset token in the repository: {}", token);
            ResetTokenModel resetToken = passwordRepository.findByToken(token);

            if (resetToken == null) {
                log.warn("Reset token not found: {}", token);
                return new ResponseEntity<>(Utilities.generateResponse(HttpStatus.BAD_REQUEST, "Invalid token"),
                        HttpStatus.BAD_REQUEST);
            }

            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                log.warn("Reset token expired: {}", token);
                return new ResponseEntity<>(Utilities.generateResponse(HttpStatus.BAD_REQUEST, "Token expired"),
                        HttpStatus.BAD_REQUEST);
            }

            log.info("Reset token is valid. Fetching associated user.");
            UserModel user = resetToken.getUser();
            log.info("Encoding new password for user with email: {}", user.getEmail());
            user.setPassword(passwordEncoder.encode(newPassword));

            log.info("Deleting reset token from repository for token: {}", token);
            passwordRepository.delete(resetToken);

            log.info("Updating user status to 'Active' for email: {}", user.getEmail());
            user.setStatus("Active");
            this.userService.save(user);

            log.info("Account activated successfully for user with email: {}", user.getEmail());
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
