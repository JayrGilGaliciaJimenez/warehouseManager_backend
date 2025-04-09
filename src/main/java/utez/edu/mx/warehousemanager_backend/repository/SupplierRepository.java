package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    @Query(value = "SELECT * FROM suppliers WHERE uuid=:uuid", nativeQuery = true)
    Supplier findByUuid(String uuid);

    @Query(value = "SELECT COUNT(s) FROM Supplier s WHERE s.name LIKE %:name%")
    int countCoincidencesByName(String name);

    boolean existsByEmail(String email);

}
