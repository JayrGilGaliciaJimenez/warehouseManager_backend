package utez.edu.mx.warehousemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import utez.edu.mx.warehousemanager.model.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {

}
