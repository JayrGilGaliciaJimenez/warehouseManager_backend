package utez.edu.mx.warehousemanager_backend.controller.Stock;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.model.Stock;
import utez.edu.mx.warehousemanager_backend.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Stock>>> findAll() {
        return stockService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Stock>> findByUuid(@PathVariable String uuid){
        return stockService.findByUuid(uuid);
    }


}
