package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.ProductEntry.ProductEntryDto;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;
import utez.edu.mx.warehousemanager_backend.repository.CategoryRepository;
import utez.edu.mx.warehousemanager_backend.repository.ProductEntryRepository;
import utez.edu.mx.warehousemanager_backend.repository.SuplierRepository;

import java.util.List;

@Service
public class ProductEntryService {
    private final ProductEntryRepository productEntryRepository;
    private final CategoryRepository categoryRepository;
    private final SuplierRepository suplierRepository;

    public ProductEntryService(ProductEntryRepository productEntryRepository, CategoryRepository categoryRepository, SuplierRepository suplierRepository) {
        this.productEntryRepository = productEntryRepository;
        this.categoryRepository = categoryRepository;
        this.suplierRepository = suplierRepository;
    }

    public ResponseEntity<ApiResponse<ProductEntry>> save(ProductEntryDto dto) {
        ProductEntry productEntry = ProductEntry.builder()
                .productName(dto.getProductName())
                .suplier(suplierRepository.findById(dto.getSuplierId()).orElse(null))
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .totalAmount(dto.getTotalAmount())
                .measurementUnit(dto.getMeasurementUnit())
                .build();

        ApiResponse<ProductEntry> response = new ApiResponse<>(
                productEntryRepository.save(productEntry),
                "New product entry registered succesfully",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<ProductEntry>>> findAll(){
        ApiResponse<List<ProductEntry>> response = new ApiResponse<>(
                productEntryRepository.findAll(),
                "All product entries list",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<ProductEntry>> findByUuid(String uuid){
        if(productEntryRepository.findByUuid(uuid) != null){
            ApiResponse<ProductEntry> response = new ApiResponse<>(
                    productEntryRepository.findByUuid(uuid),
                    "Product entry found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            ApiResponse<ProductEntry> response = new ApiResponse<>(
                    "Product entry not found",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteByUuid(String uuid) {
        ProductEntry productEntry =  productEntryRepository.findByUuid(uuid);
        if(productEntry != null) {
            ApiResponse<Void> response = new ApiResponse<>(
                    "Product entry deleted",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {
            ApiResponse<Void> response = new ApiResponse<>(
                    "Product entry not found",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}