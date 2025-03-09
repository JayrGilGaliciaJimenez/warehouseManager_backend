package utez.edu.mx.warehousemanager_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import utez.edu.mx.warehousemanager_backend.model.UserModel;
import utez.edu.mx.warehousemanager_backend.repository.IUserRepository;

@Service
@Primary
@Transactional
public class UserService {

    private final IUserRepository repository;

    UserService(IUserRepository repository) {
        this.repository = repository;
    }

    public List<UserModel> getAll() {
        return this.repository.findAll(Sort.by("id").descending());
    }

    public UserModel findByUuid(UUID uuid) {
        Optional<UserModel> optional = repository.findByUuid(uuid);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public UserModel findByEmail(String email) {
        return this.repository.findByEmail(email);
    }

    public void save(UserModel user) {
        this.repository.save(user);
    }

    public void delete(UUID uuid) {
        Optional<UserModel> optional = repository.findByUuid(uuid);
        if (optional.isPresent()) {
            this.repository.delete(optional.get());
        }
    }
}
