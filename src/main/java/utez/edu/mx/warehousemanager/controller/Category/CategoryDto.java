package utez.edu.mx.warehousemanager.controller.Category;

import lombok.Value;
import java.io.Serializable;

/**
 * DTO for {@link utez.edu.mx.warehousemanager.model.Category}
 */
@Value
public class CategoryDto implements Serializable {
    String name;
}
