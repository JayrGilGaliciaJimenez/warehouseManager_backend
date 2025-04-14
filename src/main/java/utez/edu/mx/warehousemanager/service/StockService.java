package utez.edu.mx.warehousemanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager.config.ApiResponse;
import utez.edu.mx.warehousemanager.model.Stock;
import utez.edu.mx.warehousemanager.repository.StockRepository;

import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public ResponseEntity<ApiResponse<Stock>> findByUuid (String uuid){
        if(stockRepository.findByUuid(uuid) != null) {
            ApiResponse<Stock> response = new ApiResponse<>(
                    stockRepository.findByUuid(uuid),
                    "Stock found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Stock> response = new ApiResponse<>(
                    "Stock not found",
                    "E-02", // not found
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<List<Stock>>> findAll() {
        ApiResponse<List<Stock>> response = new ApiResponse<>(
                stockRepository.findAll(),
                "All stock list",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}
