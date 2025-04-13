package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.ProductOut;

import java.util.List;
import java.util.UUID;

public interface ProductOutRepository extends JpaRepository<ProductOut, Integer> {
    @Query(value = "SELECT * FROM product_outs WHERE uuid=:uuid", nativeQuery = true)
    ProductOut findByUuid(String uuid);

    ProductOut findByUuid(UUID uuid);

    List<ProductOut> findAllByRelatedUserUUID(UUID uuid);



}
