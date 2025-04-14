package utez.edu.mx.warehousemanager_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import utez.edu.mx.warehousemanager_backend.model.RoleModel;

public interface IRoleRepository extends JpaRepository<RoleModel, Integer> {
    Optional<RoleModel> findByName(String name);
}
