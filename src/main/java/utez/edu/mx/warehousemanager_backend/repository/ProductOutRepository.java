package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.ProductOut;

public interface ProductOutRepository extends JpaRepository<ProductOut, Integer> {
    @Query(value = "SELECT * FROM product_outs WHERE uuid=:uuid", nativeQuery = true)
    ProductOut findByUuid(String uuid);
}
