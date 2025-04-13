package utez.edu.mx.warehousemanager_backend.controller.ProductOut;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;
import utez.edu.mx.warehousemanager_backend.model.ProductOut;
import utez.edu.mx.warehousemanager_backend.service.ProductOutService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productOut")
public class ProductOutController {
    private final ProductOutService productOutService;

    public ProductOutController(ProductOutService productOutService) {
        this.productOutService = productOutService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<List<ProductOutDto>>> save(@RequestBody OutDto dto){
        return productOutService.save(dto);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ProductOut>>> findAll(){
        return productOutService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductOut>> findByUuid(@PathVariable String uuid){
        return productOutService.findByUuid(uuid);
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<ApiResponse<List<ProductOut>>>getEntriesByUser(@PathVariable UUID uuid){
        return productOutService.findByUser(uuid);
    }
    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable UUID uuid){
        return productOutService.deleteByUuid(uuid);
    }

    @GetMapping("/grouped/user/{uuid}")
    public ResponseEntity<ApiResponse<List<OutGroupDto>>> getGroupedOutsByUser(@PathVariable UUID uuid) {
        return productOutService.findGroupedByUser(uuid);
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<List<OutGroupDto>>> getAllGroupedOuts() {
        return productOutService.findAllGrouped();
    }

}
