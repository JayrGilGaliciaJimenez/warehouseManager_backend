package utez.edu.mx.warehousemanager.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public ApiResponse(String message, String errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
    }

}