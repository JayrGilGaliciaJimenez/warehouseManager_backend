package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.Supplier;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    @Query(value = "SELECT * FROM suppliers WHERE uuid=:uuid", nativeQuery = true)
    Supplier findByUuid(UUID uuid);


    boolean existsByEmail(String email);

    boolean existsByNameIsLike(String name);
}
