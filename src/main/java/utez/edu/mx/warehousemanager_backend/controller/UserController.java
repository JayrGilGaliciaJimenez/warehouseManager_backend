package utez.edu.mx.warehousemanager_backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import utez.edu.mx.warehousemanager_backend.model.EmailModel;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.model.UserStatusModel;
import utez.edu.mx.warehousemanager_backend.service.EmailService;
import utez.edu.mx.warehousemanager_backend.service.UserService;
import utez.edu.mx.warehousemanager_backend.utils.Utilities;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String RECORD_NOT_FOUND = "Record not found.";
    private static final String INTERNAL_SERVER_ERROR = "An internal server error occurred.";

    UserController(UserService userService, EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Test
    @GetMapping("/user/test")
    public String test() {
        return "Ok";
    }

    // GetAll
    @GetMapping("/user/list")
    public List<UserModel> users() {
        return this.userService.getAll();
    }

    // GetByUUID
    @GetMapping("/user/{uuid}")
    public UserModel getByUuid(@PathVariable("uuid") UUID uuid) {
        return this.userService.findByUuid(uuid);
    }

    // Register User
    @PostMapping("/user/register")
    public ResponseEntity<Object> createUser(@RequestBody UserModel request) {
        try {
            UserModel existingUser = userService.findByEmail(request.getEmail());
            if (existingUser != null) {
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, "Email already registered");
            }
            String temporaryPassword = request.getPassword();

            request.setPassword(passwordEncoder.encode(request.getPassword()));
            this.userService.save(request);

            EmailModel emailModel = new EmailModel();
            emailModel.setRecipient(request.getEmail());
            emailModel.setSubject("Registro Exitoso");
            emailModel.setMessage("Hello, " + request.getName() + " " + request.getLastname());
            emailModel.setEmail(request.getEmail());
            emailModel.setPassword(temporaryPassword);
            emailService.sendEmail(emailModel);
            return Utilities.generateResponse(HttpStatus.OK, "Record created succesfully");
        } catch (Exception e) {
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    // Deactivate User
    @PutMapping("/user/deactivate/{uuid}")
    public ResponseEntity<Object> deactivateUser(@PathVariable("uuid") UUID uuid) {
        try {
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            } else {
                UserStatusModel deactivateStatus = new UserStatusModel();
                deactivateStatus.setId(2);
                user.setStatus(deactivateStatus);
                this.userService.save(user);
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
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            } else {
                UserStatusModel activateStatus = new UserStatusModel();
                activateStatus.setId(1);
                user.setStatus(activateStatus);
                this.userService.save(user);
                return Utilities.generateResponse(HttpStatus.OK, "User activated successfully");
            }
        } catch (Exception e) {
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    // Delete User
    @DeleteMapping("/user/{uuid}")
    public ResponseEntity<Object> deleteUser(@PathVariable("uuid") UUID uuid) {
        try {
            UserModel user = this.getByUuid(uuid);
            if (user == null) {
                return Utilities.generateResponse(HttpStatus.BAD_REQUEST, RECORD_NOT_FOUND);
            } else {
                this.userService.delete(uuid);
                return Utilities.generateResponse(HttpStatus.OK, "Record deleted succesfully");
            }
        } catch (Exception e) {
            return Utilities.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
        }
    }
}
