package utez.edu.mx.warehousemanager_backend.controller.Suplier;

import lombok.Value;
import utez.edu.mx.warehousemanager_backend.model.Supplier;

import java.io.Serializable;

/**
 * DTO for {@link Supplier}
 */
@Value
public class SuplierDto implements Serializable {
    String name;
    String email;
    Integer relatedUserId;
}