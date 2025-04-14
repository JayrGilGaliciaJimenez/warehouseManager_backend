package utez.edu.mx.warehousemanager_backend.controller.ProductEntry;


import lombok.Value;
import java.io.Serializable;
import java.util.List;

@Value
public class EntryDto implements Serializable {
    Integer supplierId;
    List<ProductEntryDto> productEntryList;

}
