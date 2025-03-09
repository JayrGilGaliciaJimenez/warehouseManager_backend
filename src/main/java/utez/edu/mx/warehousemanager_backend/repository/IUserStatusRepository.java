package utez.edu.mx.warehousemanager_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import utez.edu.mx.warehousemanager_backend.model.UserStatusModel;

public interface IUserStatusRepository extends JpaRepository<UserStatusModel, Integer> {
    Optional<UserStatusModel> findByName(String name);
}
