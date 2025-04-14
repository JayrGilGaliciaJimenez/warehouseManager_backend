package utez.edu.mx.warehousemanager.controller.ProductEntry;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager.config.ApiResponse;
import utez.edu.mx.warehousemanager.model.ProductEntry;
import utez.edu.mx.warehousemanager.service.ProductEntryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productEntry")
public class ProductEntryController {
    private final ProductEntryService productEntryService;

    public ProductEntryController(ProductEntryService productEntryService) {
        this.productEntryService = productEntryService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<List<ProductEntryDto>>> save(@RequestBody EntryDto dto){
        return productEntryService.save(dto);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ProductEntry>>> findAll(){
        return productEntryService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductEntry>> findByUuid(@PathVariable UUID uuid){
        return productEntryService.findByUuid(uuid);
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<ApiResponse<List<ProductEntry>>>getEntriesByUser(@PathVariable UUID uuid){
        return productEntryService.findByUser(uuid);
    }

    @GetMapping("/grouped/user/{uuid}")
    public ResponseEntity<ApiResponse<List<EntryGroupDto>>> getGroupedEntriesByUser(@PathVariable UUID uuid) {
        return productEntryService.findGroupedEntriesByUser(uuid);
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<List<EntryGroupDto>>> getAllGroupedEntries( ) {
        return productEntryService.findAllGroupedEntries();
    }


    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable UUID uuid){
        return productEntryService.deleteByUuid(uuid);
    }


}
