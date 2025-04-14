package utez.edu.mx.warehousemanager.controller.productout;
import lombok.Value;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link utez.edu.mx.warehousemanager.model.ProductOut}
 */
@Value
public class ProductOutDto implements Serializable {
    String productName;
    String measurementUnit;
    int quantity;
    double unitPrice;
    double totalAmount;
    String receiverName;
    UUID relatedUserUUID;
}