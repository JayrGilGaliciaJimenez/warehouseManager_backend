package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.ProductEntry.EntryDto;
import utez.edu.mx.warehousemanager_backend.controller.ProductEntry.EntryGroupDto;
import utez.edu.mx.warehousemanager_backend.controller.ProductEntry.ProductEntryDto;
import utez.edu.mx.warehousemanager_backend.model.ProductEntry;
import utez.edu.mx.warehousemanager_backend.repository.CategoryRepository;
import utez.edu.mx.warehousemanager_backend.repository.ProductEntryRepository;
import utez.edu.mx.warehousemanager_backend.repository.SupplierRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductEntryService {
    private final ProductEntryRepository productEntryRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductEntryService(ProductEntryRepository productEntryRepository, CategoryRepository categoryRepository, SupplierRepository supplierRepository) {
        this.productEntryRepository = productEntryRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    public ResponseEntity<ApiResponse<List<ProductEntryDto>>> save(EntryDto entryDto) {
        for (ProductEntryDto productEntryDto : entryDto.getProductEntryList()) {
            String validationError = validateProductEntry(productEntryDto);
            if (validationError != null) {
                ApiResponse<List<ProductEntryDto>> response = new ApiResponse<>(
                        validationError,
                        "E-01", // invalid input data
                        HttpStatus.CONFLICT
                );
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            ProductEntry productEntry = ProductEntry.builder()
                    .productName(productEntryDto.getProductName())
                    .supplier(supplierRepository.findById(productEntryDto.getSupplierId()).orElse(null))
                    .category(categoryRepository.findById(productEntryDto.getCategoryId()).orElse(null))
                    .quantity(productEntryDto.getQuantity())
                    .unitPrice(productEntryDto.getUnitPrice())
                    .totalAmount(productEntryDto.getQuantity() * productEntryDto.getUnitPrice())
                    .entryDate(LocalDateTime.now())
                    .measurementUnit(productEntryDto.getMeasurementUnit())
                    .relatedUserUUID(productEntryDto.getRelatedUserUUID())
                    .build();
            productEntryRepository.save(productEntry);
        }

        ApiResponse<List<ProductEntryDto>> response = new ApiResponse<>(
                entryDto.getProductEntryList(),
                "New product entries registered successfully",
                HttpStatus.OK
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private String validateProductEntry(ProductEntryDto productEntryDto) {
        if (productEntryDto.getProductName() == null || productEntryDto.getProductName().isBlank()) {
            return "Product name cannot be empty or blank.";
        }
        if (productEntryDto.getMeasurementUnit() == null || productEntryDto.getMeasurementUnit().isBlank()) {
            return "Measurement unit cannot be empty or blank.";
        }
        if (productEntryDto.getQuantity() <= 0) {
            return "Quantity must be greater than zero.";
        }
        if (productEntryDto.getUnitPrice() <= 0) {
            return "Unit price must be greater than zero.";
        }
        if (productEntryDto.getSupplierId() == null) {
            return "Supplier ID cannot be null.";
        }
        if (productEntryDto.getCategoryId() == null) {
            return "Category ID cannot be null.";
        }
        return null;
    }

    public ResponseEntity<ApiResponse<List<ProductEntry>>> findAll(){
        ApiResponse<List<ProductEntry>> response = new ApiResponse<>(
                productEntryRepository.findAll(),
                "All product entries list",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<ProductEntry>> findByUuid(UUID uuid){
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
                    "E-02", // not found
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteByUuid(UUID uuid){
        ProductEntry productEntry = productEntryRepository.findByUuid(uuid);
        if (productEntry != null){
            productEntryRepository.delete(productEntry);
            ApiResponse<String> response = new ApiResponse<>(
                    "Product entry deleted",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        ApiResponse<String> response = new ApiResponse<>(
                "Product entry not found",
                "E-02", // not found
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ApiResponse<List<ProductEntry>>> findByUser(UUID uuid) {
        List<ProductEntry> productEntries = productEntryRepository.findAllByRelatedUserUUID(uuid);
        if (!productEntries.isEmpty()) {
            ApiResponse<List<ProductEntry>> response = new ApiResponse<>(
                    productEntries,
                    "Product entries found for the related user",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<ProductEntry>> response = new ApiResponse<>(
                    "No product entries found for the related user",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<List<EntryGroupDto>>> findGroupedEntriesByUser(UUID uuid) {
        List<ProductEntry> entries = productEntryRepository.findAllByRelatedUserUUID(uuid);

        Map<String, List<ProductEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getEntryDate().withSecond(0).withNano(0).toString()));

        List<EntryGroupDto> groupedEntries = grouped.values().stream()
                .map(productList -> {
                    return new EntryGroupDto(
                            productList.get(0).getEntryDate().withSecond(0).withNano(0),
                            productList.get(0).getSupplier().getName(),
                            productList
                    );
                }).toList();

        ApiResponse<List<EntryGroupDto>> response = new ApiResponse<>(
                groupedEntries,
                "Grouped entries found for the related user",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<EntryGroupDto>>> findAllGroupedEntries() {
        List<ProductEntry> entries = productEntryRepository.findAll();

        List<EntryGroupDto> groupedEntries = groupEntries(entries);

        return ResponseEntity.ok(new ApiResponse<>(groupedEntries, "Grouped entries for admin", HttpStatus.OK));
    }

    private List<EntryGroupDto> groupEntries(List<ProductEntry> entries) {
        Map<String, List<ProductEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(e ->
                        e.getEntryDate().withSecond(0).withNano(0).toString()
                ));

        return grouped.values().stream()
                .map(group -> {
                    return new EntryGroupDto(
                            group.get(0).getEntryDate().withSecond(0).withNano(0),
                            group.get(0).getSupplier() != null ? group.get(0).getSupplier().getName() : "Sin proveedor",
                            group
                    );
                }).collect(Collectors.toList());
    }

}