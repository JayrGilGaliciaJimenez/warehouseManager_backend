package utez.edu.mx.warehousemanager.controller.productout;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
@SuppressWarnings("squid:S1948")
public class OutDto implements Serializable {
    String receiverName;
    List<ProductOutDto> productOutList;
}
