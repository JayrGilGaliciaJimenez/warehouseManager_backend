package utez.edu.mx.warehousemanager.controller.productentry;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link utez.edu.mx.warehousemanager.model.ProductEntry}
 */
@Value
public class ProductEntryDto implements Serializable {
    String productName;
    Integer supplierId;
    Integer categoryId;
    int quantity;
    double unitPrice;
    double totalAmount;
    String measurementUnit;
    UUID relatedUserUUID;

}