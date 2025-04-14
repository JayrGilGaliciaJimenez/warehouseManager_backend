package utez.edu.mx.warehousemanager_backend.controller.Stock;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link utez.edu.mx.warehousemanager_backend.model.Stock}
 */
@Value
public class StockDto implements Serializable {
    String productName;
    String measurementUnit;
    int quantity;
    double unitPrice;
    double totalAmount;
    Integer supplierId;
}

