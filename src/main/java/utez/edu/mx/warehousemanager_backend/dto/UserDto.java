package utez.edu.mx.warehousemanager_backend.dto;

import java.sql.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private UUID uuid;
    private String name;
    private String lastname;
    private String email;
    private Date creationDate;
    private String status;
    private RoleDto role;
}
