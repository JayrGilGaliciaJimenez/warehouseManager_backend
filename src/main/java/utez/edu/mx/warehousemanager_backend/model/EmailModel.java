package utez.edu.mx.warehousemanager_backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailModel {

    private String recipient;
    private String subject;
    private String message;
}
