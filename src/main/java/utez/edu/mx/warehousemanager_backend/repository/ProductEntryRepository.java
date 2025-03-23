package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;

public interface ProductEntryRepository extends JpaRepository<ProductEntry,Integer> {

    @Query(value = "SELECT * FROM product_entries WHERE uuid = :uuid", nativeQuery = true)
    ProductEntry findByUuid(String uuid);

}
