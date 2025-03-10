package utez.edu.mx.warehousemanager_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import utez.edu.mx.warehousemanager_backend.model.ResetTokenModel;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.repository.IPasswordResetToken;
import utez.edu.mx.warehousemanager_backend.repository.IUserRepository;

@Service
@Primary
@Transactional
public class UserService {

    private final IUserRepository userRepository;
    private final IPasswordResetToken passwordRepository;

    UserService(IUserRepository userRepository, IPasswordResetToken passwordRepository) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
    }

    public List<UserModel> getAll() {
        return this.userRepository.findAll(Sort.by("id").descending());
    }

    public UserModel findByUuid(UUID uuid) {
        Optional<UserModel> optional = userRepository.findByUuid(uuid);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public UserModel findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void save(UserModel user) {
        this.userRepository.save(user);
    }

    public void delete(UUID uuid) {
        Optional<UserModel> optional = userRepository.findByUuid(uuid);
        if (optional.isPresent()) {
            this.userRepository.delete(optional.get());
        }
    }

    public void savePasswordResetToken(UserModel user, String token) {
        ResetTokenModel resetToken = new ResetTokenModel();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        passwordRepository.save(resetToken);
    }
}
