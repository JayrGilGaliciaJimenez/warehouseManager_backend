package utez.edu.mx.warehousemanager_backend.controller.ProductEntry;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;
import utez.edu.mx.warehousemanager_backend.service.ProductEntryService;

import java.util.List;

@RestController
@RequestMapping("/api/productEntry")
public class ProductEntryController {
    private final ProductEntryService productEntryService;

    public ProductEntryController(ProductEntryService productEntryService) {
        this.productEntryService = productEntryService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<ProductEntry>> save(@RequestBody ProductEntryDto dto){
        return productEntryService.save(dto);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ProductEntry>>> findAll(){
        return productEntryService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductEntry>> findByUuid(@PathVariable String uuid){
        return productEntryService.findByUuid(uuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String  uuid){
        return productEntryService.deleteByUuid(uuid);
    }


}
