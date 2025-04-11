package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.ProductOut.OutDto;
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

    public ResponseEntity<ApiResponse<List<ProductOutDto>>> save (OutDto outDto){
        for (ProductOutDto productOutDto : outDto.getProductOutList()) {
            String validationError = validateProductOut(productOutDto);
            if (validationError != null) {
                ApiResponse<List<ProductOutDto>> response = new ApiResponse<>(
                        validationError,
                        HttpStatus.CONFLICT
                );
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            ProductOut productOut = ProductOut.builder()
                    .productName(productOutDto.getProductName())
                    .quantity(productOutDto.getQuantity())
                    .unitPrice(productOutDto.getUnitPrice())
                    .totalAmount(productOutDto.getQuantity() * productOutDto.getUnitPrice())
                    .outDate(LocalDateTime.now())
                    .measurementUnit(productOutDto.getMeasurementUnit())
                    .receiverName(productOutDto.getReceiverName())
                    .relatedUserUUID(productOutDto.getRelatedUserUUID())
                    .build();
            productOutRepository.save(productOut);
        }

        ApiResponse<List<ProductOutDto>> response = new ApiResponse<>(
                outDto.getProductOutList(),
                "New product outs registered successfully",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String validateProductOut(ProductOutDto productOutDto) {
        // Implement validation logic here
        return null;
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
