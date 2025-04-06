package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.Supplier.SupplierDto;
import utez.edu.mx.warehousemanager_backend.model.Supplier;
import utez.edu.mx.warehousemanager_backend.repository.SupplierRepository;
import java.util.List;


@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public ResponseEntity<ApiResponse<Supplier>> save(SupplierDto dto) {
        int existingSuppliersCount = supplierRepository.countCoincidencesByName(dto.getName().trim());
        if (existingSuppliersCount > 0) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Supplier already exists",
                    HttpStatus.CONFLICT
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Supplier supplier = Supplier.builder()
                .name(dto.getName().trim())
                .email(dto.getEmail().trim())
                .relatedUserId(dto.getRelatedUserId())
                .build();
        ApiResponse<Supplier> response = new ApiResponse<>(
                supplierRepository.save(supplier),
                "Supplier created",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
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
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }
    }

}
