package utez.edu.mx.warehousemanager_backend.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}
