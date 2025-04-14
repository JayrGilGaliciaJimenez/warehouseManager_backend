package utez.edu.mx.warehousemanager.controller.ProductOut;

import lombok.Value;
import utez.edu.mx.warehousemanager.model.ProductOut;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class OutGroupDto {
    private LocalDateTime outDate;
    private String receiverName;
    private List<ProductOut> outs;
}
