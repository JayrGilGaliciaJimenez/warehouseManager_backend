package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.ProductOut.ProductOutDto;
import utez.edu.mx.warehousemanager_backend.model.ProductOut;
import utez.edu.mx.warehousemanager_backend.repository.ProductOutRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductOutService {
    private final ProductOutRepository productOutRepository;

    public ProductOutService(ProductOutRepository productOutRepository) {
        this.productOutRepository = productOutRepository;
    }

    public ResponseEntity<ApiResponse<ProductOut>> save (ProductOutDto dto) {
        ProductOut savedProductOut = ProductOut.builder()
                .productName(dto.getProductName())
                .unitPrice(dto.getUnitPrice())
                .quantity(dto.getQuantity())
                .totalAmount(dto.getTotalAmount())
                .measurementUnit(dto.getMeasurementUnit())
                .outDate(LocalDateTime.now())
                .reciverName(dto.getReciverName())
                .build();

        ApiResponse<ProductOut> response = new ApiResponse<>(
                productOutRepository.save(savedProductOut),
                "New product out created",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<ProductOut>> findByUuid(String uuid){
        if(productOutRepository.findByUuid(uuid) != null){
            ApiResponse<ProductOut> response = new ApiResponse<>(
                    productOutRepository.findByUuid(uuid),
                    "Product out found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            ApiResponse<ProductOut> response = new ApiResponse<>(
                    "Product out not found",
                    "E-02", // not found
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<ApiResponse<List<ProductOut>>> findAll() {
        ApiResponse<List<ProductOut>> response = new ApiResponse<>(
                productOutRepository.findAll(),
                "All product outs list",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
