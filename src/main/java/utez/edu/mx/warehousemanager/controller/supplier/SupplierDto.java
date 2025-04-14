package utez.edu.mx.warehousemanager.controller.supplier;

import lombok.Value;
import utez.edu.mx.warehousemanager.model.Supplier;

import java.io.Serializable;

/**
 * DTO for {@link Supplier}
 */
@Value
public class SupplierDto implements Serializable {
    String name;
    String email;
}