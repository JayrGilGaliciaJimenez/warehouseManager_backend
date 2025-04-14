package utez.edu.mx.warehousemanager.controller.ProductEntry;


import lombok.Value;
import java.io.Serializable;
import java.util.List;

@Value
@SuppressWarnings("squid:S1948")
public class EntryDto implements Serializable {
    Integer supplierId;
    List<ProductEntryDto> productEntryList;

}
