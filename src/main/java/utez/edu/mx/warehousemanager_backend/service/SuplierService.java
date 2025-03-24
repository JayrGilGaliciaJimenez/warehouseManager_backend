package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.Suplier.SuplierDto;
import utez.edu.mx.warehousemanager_backend.model.Suplier;
import utez.edu.mx.warehousemanager_backend.repository.SuplierRepository;
import java.util.List;


@Service
public class SuplierService {
    private final SuplierRepository suplierRepository;

    public SuplierService(SuplierRepository suplierRepository) {
        this.suplierRepository = suplierRepository;
    }

    public ResponseEntity<ApiResponse<Suplier>> save(SuplierDto dto) {
        int existingSupliersCount = suplierRepository.countCoincidencesByName(dto.getName().trim());
        if (existingSupliersCount > 0) {
            ApiResponse<Suplier> response = new ApiResponse<>(
                    "Suplier already exists",
                    HttpStatus.CONFLICT
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Suplier suplier = Suplier.builder()
                .name(dto.getName().trim())
                .email(dto.getEmail().trim())
                .build();
        ApiResponse<Suplier> response = new ApiResponse<>(
                suplierRepository.save(suplier),
                "Suplier created",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<Suplier>>> findAll() {
        if (suplierRepository.findAll().isEmpty()) {
            ApiResponse<List<Suplier>> response = new ApiResponse<>(
                    "No supliers registred",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            ApiResponse<List<Suplier>> response = new ApiResponse<>(
                    suplierRepository.findAll(),
                    "All supliers list",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    public ResponseEntity<ApiResponse<Suplier>> findByUuid(String uuid) {
        if (suplierRepository.findByUuid(uuid) != null) {
            ApiResponse<Suplier> response = new ApiResponse<>(
                    suplierRepository.findByUuid(uuid),
                    "Suplier found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Suplier> response = new ApiResponse<>(
                    "Suplier not found",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<ApiResponse<Void>> deleteByUuid(String uuid) {
        Suplier suplier = suplierRepository.findByUuid(uuid);
        if (suplier != null) {
            suplierRepository.deleteById(suplier.getId());
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
