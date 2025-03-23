package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.Suplier;

public interface SuplierRepository extends JpaRepository<Suplier, Integer> {

    @Query(value = "SELECT * FROM supliers WHERE uuid=:uuid", nativeQuery = true)
    Suplier findByUuid(String uuid);


}
