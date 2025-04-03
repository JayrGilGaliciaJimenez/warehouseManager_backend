package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.Suplier.SuplierDto;
import utez.edu.mx.warehousemanager_backend.model.Supplier;
import utez.edu.mx.warehousemanager_backend.repository.SuplierRepository;
import java.util.List;


@Service
public class SuplierService {
    private final SuplierRepository suplierRepository;

    public SuplierService(SuplierRepository suplierRepository) {
        this.suplierRepository = suplierRepository;
    }

    public ResponseEntity<ApiResponse<Supplier>> save(SuplierDto dto) {
        int existingSupliersCount = suplierRepository.countCoincidencesByName(dto.getName().trim());
        if (existingSupliersCount > 0) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Suplier already exists",
                    HttpStatus.CONFLICT
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Supplier supplier = Supplier.builder()
                .name(dto.getName().trim())
                .email(dto.getEmail().trim())
                .build();
        ApiResponse<Supplier> response = new ApiResponse<>(
                suplierRepository.save(supplier),
                "Suplier created",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<Supplier>>> findAll() {
        if (suplierRepository.findAll().isEmpty()) {
            ApiResponse<List<Supplier>> response = new ApiResponse<>(
                    "No supliers registred",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            ApiResponse<List<Supplier>> response = new ApiResponse<>(
                    suplierRepository.findAll(),
                    "All supliers list",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    public ResponseEntity<ApiResponse<Supplier>> findByUuid(String uuid) {
        if (suplierRepository.findByUuid(uuid) != null) {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    suplierRepository.findByUuid(uuid),
                    "Suplier found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Supplier> response = new ApiResponse<>(
                    "Suplier not found",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<ApiResponse<Void>> deleteByUuid(String uuid) {
        Supplier supplier = suplierRepository.findByUuid(uuid);
        if (supplier != null) {
            suplierRepository.deleteById(supplier.getId());
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
