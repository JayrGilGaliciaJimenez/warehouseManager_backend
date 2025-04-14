package utez.edu.mx.warehousemanager.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailModel {

    private String recipient;
    private String subject;
    private String message;
    private String templateName;
}