package utez.edu.mx.warehousemanager_backend.controller.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.model.Supplier;
import utez.edu.mx.warehousemanager_backend.service.SupplierService;
import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Supplier>> save (@RequestBody SupplierDto dto){
        return supplierService.save(dto);

    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Supplier>>> findAll(){
        return supplierService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Supplier>> findByUuid(@PathVariable String uuid){
        return supplierService.findByUuid(uuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String uuid){
        return supplierService.deleteByUuid(uuid);
    }



}
