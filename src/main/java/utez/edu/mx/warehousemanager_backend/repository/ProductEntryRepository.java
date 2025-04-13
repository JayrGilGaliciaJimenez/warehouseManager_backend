package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;

import java.util.List;
import java.util.UUID;

public interface ProductEntryRepository extends JpaRepository<ProductEntry,Integer> {

    ProductEntry findByUuid(UUID uuid);
    List <ProductEntry> findAllByRelatedUserUUID(UUID uuid);
}
