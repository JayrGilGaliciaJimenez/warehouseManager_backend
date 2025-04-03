package utez.edu.mx.warehousemanager_backend.controller.ProductEntry;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link utez.edu.mx.warehousemanager_backend.model.ProductEntry}
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
    Integer relatedUserId;
}