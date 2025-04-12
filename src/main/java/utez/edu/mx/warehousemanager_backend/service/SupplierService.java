package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.Supplier.SupplierDto;
import utez.edu.mx.warehousemanager_backend.model.Supplier;
import utez.edu.mx.warehousemanager_backend.repository.SupplierRepository;

import java.util.List;
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
                    "E-01", // invalid input data
                    HttpStatus.BAD_REQUEST
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (supplierRepository.existsByEmail(supplierDto.getEmail())) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Ya existe un proveedor con ese email",
                    "E-03", // duplicate data
                    HttpStatus.CONFLICT
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        if (supplierRepository.existsByNameIsLike(supplierDto.getName())) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Ya existe un proveedor con ese nombre",
                    "E-03", // duplicate data
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
                    "E-02", // not found
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
        if (supplierRepository.findByUuid(uuid) != null) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    supplierRepository.findByUuid(uuid),
                    "Supplier found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Supplier not found",
                    "E-02", // not found
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteByUuid(String uuid) {
        Supplier supplier = supplierRepository.findByUuid(uuid);
        if (supplier != null) {
            supplierRepository.deleteById(supplier.getId());
            ApiResponse<Void> response = new ApiResponse<>(
                    "Suplier deleted",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {

            ApiResponse<Void> response = new ApiResponse<>(
                    "Suplier not found",
                    "E-02", // not found
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }
    }

}
