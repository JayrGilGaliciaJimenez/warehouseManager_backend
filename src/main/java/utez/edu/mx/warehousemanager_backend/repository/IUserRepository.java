package utez.edu.mx.warehousemanager_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import utez.edu.mx.warehousemanager_backend.model.UserModel;

public interface IUserRepository extends JpaRepository<UserModel, Integer> {

    UserModel findByEmail(String email);

    Optional<UserModel> findByUuid(UUID uuid);
}
