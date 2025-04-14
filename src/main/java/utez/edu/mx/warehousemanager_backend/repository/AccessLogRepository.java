package utez.edu.mx.warehousemanager_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utez.edu.mx.warehousemanager_backend.model.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {

}
