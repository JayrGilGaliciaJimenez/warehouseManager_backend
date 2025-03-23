package utez.edu.mx.warehousemanager_backend.controller.Suplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.model.Suplier;
import utez.edu.mx.warehousemanager_backend.service.SuplierService;

import java.util.List;

@RestController
@RequestMapping("/api/suplier")
public class SuplierController {
    private final SuplierService suplierService;

    public SuplierController(SuplierService suplierService) {
        this.suplierService = suplierService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Suplier>> save (@RequestBody SuplierDto dto){
        return suplierService.save(dto);

    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Suplier>>> findAll(){
        return suplierService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Suplier>> findByUuid(@PathVariable String uuid){
        return suplierService.findByUuid(uuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String uuid){
        return suplierService.deleteByUuid(uuid);
    }



}
