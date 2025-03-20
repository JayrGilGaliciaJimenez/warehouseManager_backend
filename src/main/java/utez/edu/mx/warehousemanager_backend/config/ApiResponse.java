package utez.edu.mx.warehousemanager_backend.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private String errorCode;
    private HttpStatus status;

    public ApiResponse(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
    public ApiResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}