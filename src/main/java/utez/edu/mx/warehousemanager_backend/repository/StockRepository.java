package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    @Query(value = "SELECT * FROM stock WHERE uuid=:uuid", nativeQuery = true)
    Stock findByUuid(String uuid);

}
