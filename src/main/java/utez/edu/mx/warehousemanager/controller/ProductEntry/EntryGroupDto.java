package utez.edu.mx.warehousemanager.controller.ProductEntry;

import lombok.Value;
import utez.edu.mx.warehousemanager.model.ProductEntry;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class EntryGroupDto {
    private LocalDateTime entryDate;
    private String supplier;
    private List<ProductEntry> entries;
}
