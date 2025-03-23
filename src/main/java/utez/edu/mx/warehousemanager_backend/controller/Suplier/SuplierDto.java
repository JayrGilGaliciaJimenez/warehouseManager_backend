package utez.edu.mx.warehousemanager_backend.controller.Suplier;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link utez.edu.mx.warehousemanager_backend.model.Suplier}
 */
@Value
public class SuplierDto implements Serializable {
    String name;
    String email;
    Integer relatedUserId;
}