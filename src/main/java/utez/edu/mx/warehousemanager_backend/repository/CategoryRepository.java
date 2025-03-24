package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import utez.edu.mx.warehousemanager_backend.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(value = "SELECT COUNT(c) FROM Category c WHERE c.name LIKE %:name%")
    int countCoincidencesByName(String name);

    @Query(value = "SELECT * FROM categories WHERE uuid=:uuid", nativeQuery = true)
    Category findByUuid(String uuid);



}

