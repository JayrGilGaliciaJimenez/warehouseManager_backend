package utez.edu.mx.warehousemanager_backend.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private HttpStatus status;
    private String message;
    private String errorCode;
    private T data;

}