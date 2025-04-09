package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.Supplier.SupplierDto;
import utez.edu.mx.warehousemanager_backend.model.Supplier;
import utez.edu.mx.warehousemanager_backend.repository.SupplierRepository;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public ResponseEntity<ApiResponse<Supplier>> save(SupplierDto supplierDto) {
        if (!isValidEmail(supplierDto.getEmail())) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Invalid email format.",
                    HttpStatus.BAD_REQUEST
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (supplierRepository.existsByEmail(supplierDto.getEmail())) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "A supplier with this email already exists.",
                    HttpStatus.CONFLICT
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        Supplier supplier = Supplier.builder()
                .name(supplierDto.getName())
                .email(supplierDto.getEmail())
                .build();

        supplierRepository.save(supplier);
        ApiResponse<Supplier> response = new ApiResponse<>(
                supplier,
                "Supplier saved successfully.",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public ResponseEntity<ApiResponse<List<Supplier>>> findAll() {
        if (supplierRepository.findAll().isEmpty()) {
            ApiResponse<List<Supplier>> response = new ApiResponse<>(
                    "No suppliers registred",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            ApiResponse<List<Supplier>> response = new ApiResponse<>(
                    supplierRepository.findAll(),
                    "All suppliers list",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    public ResponseEntity<ApiResponse<Supplier>> findByUuid(String uuid) {
        try {
            UUID parsedUuid = UUID.fromString(uuid);
            Supplier supplier = supplierRepository.findByUuid(parsedUuid);
            if (supplier != null) {
                return ResponseEntity.ok(new ApiResponse<>(supplier, "Supplier found", HttpStatus.OK));
            } else {
                return new ResponseEntity<>(new ApiResponse<>("Supplier not found", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("Invalid UUID format", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteByUuid(String uuid) {
        try {
            UUID parsedUuid = UUID.fromString(uuid);
            Supplier supplier = supplierRepository.findByUuid(parsedUuid);
            if (supplier != null) {
                supplierRepository.deleteById(supplier.getId());
                return new ResponseEntity<>(new ApiResponse<>("Supplier deleted", HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>("Supplier not found", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("Invalid UUID format", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
