package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.ProductOut.OutDto;
import utez.edu.mx.warehousemanager_backend.controller.ProductOut.OutGroupDto;
import utez.edu.mx.warehousemanager_backend.controller.ProductOut.ProductOutDto;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;
import utez.edu.mx.warehousemanager_backend.model.ProductOut;
import utez.edu.mx.warehousemanager_backend.repository.ProductOutRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private String validateProductOut(ProductOutDto productOutDto) {
        if (productOutDto.getProductName() == null || productOutDto.getProductName().isBlank()) {
            return "Product name cannot be empty or blank.";
        }
        if (productOutDto.getMeasurementUnit() == null || productOutDto.getMeasurementUnit().isBlank()) {
            return "Measurement unit cannot be empty or blank.";
        }
        if (productOutDto.getQuantity() <= 0) {
            return "Quantity must be greater than zero.";
        }
        if (productOutDto.getUnitPrice() <= 0) {
            return "Unit price must be greater than zero.";
        }
        if (productOutDto.getRelatedUserUUID() == null) {
            return "Related user UUID cannot be null.";
        }
        return null;
    }


    public ResponseEntity<ApiResponse<List<ProductOut>>> findAll() {
        ApiResponse<List<ProductOut>> response = new ApiResponse<>(
                productOutRepository.findAll(),
                "All product outs list",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<String>> deleteByUuid(UUID uuid){
        ProductOut productOut = productOutRepository.findByUuid(uuid);
        if (productOut != null){
            productOutRepository.delete(productOut);
            ApiResponse<String> response = new ApiResponse<>(
                    "Product out deleted",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        ApiResponse<String> response = new ApiResponse<>(
                "Product out not found",
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ApiResponse<List<ProductOut>>> findByUser(UUID uuid) {
        List<ProductOut> productOuts = productOutRepository.findAllByRelatedUserUUID(uuid);
        if (!productOuts.isEmpty()) {
            ApiResponse<List<ProductOut>> response = new ApiResponse<>(
                    productOuts,
                    "Product outs found for the related user",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<ProductOut>> response = new ApiResponse<>(
                    "No product outs found for the related user",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<List<OutGroupDto>>> findGroupedByUser(UUID uuid) {
        List<ProductOut> outs = productOutRepository.findAllByRelatedUserUUID(uuid);
        List<OutGroupDto> grouped = groupOuts(outs);
        return ResponseEntity.ok(new ApiResponse<>(grouped, "Grouped outs by user", HttpStatus.OK));
    }

    public ResponseEntity<ApiResponse<List<OutGroupDto>>> findAllGrouped() {
        List<ProductOut> outs = productOutRepository.findAll();
        List<OutGroupDto> grouped = groupOuts(outs);
        return ResponseEntity.ok(new ApiResponse<>(grouped, "Grouped outs for admin", HttpStatus.OK));
    }

    private List<OutGroupDto> groupOuts(List<ProductOut> outs) {
        return outs.stream()
                .collect(Collectors.groupingBy(o -> o.getOutDate().withSecond(0).withNano(0).toString()))
                .values()
                .stream()
                .map(group -> {
                    return new OutGroupDto(
                            group.get(0).getOutDate().withSecond(0).withNano(0),
                            group.get(0).getReceiverName(),
                            group
                    );
                }).collect(Collectors.toList());
    }

}
