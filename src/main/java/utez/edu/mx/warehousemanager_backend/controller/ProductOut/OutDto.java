package utez.edu.mx.warehousemanager_backend.controller.ProductOut;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
public class OutDto implements Serializable {
    String receiverName;
    List<ProductOutDto> productOutList;
}
