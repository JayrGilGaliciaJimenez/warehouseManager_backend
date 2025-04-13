package utez.edu.mx.warehousemanager_backend.controller.ProductEntry;

import lombok.Value;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class EntryGroupDto {
    private LocalDateTime entryDate;
    private String supplier;
    private List<ProductEntry> entries;
}
