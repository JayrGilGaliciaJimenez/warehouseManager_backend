package utez.edu.mx.warehousemanager_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_outs")
public class ProductOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private UUID uuid;
    private String productName;
    private String measurementUnit;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private LocalDateTime outDate = LocalDateTime.now();
    private String reciverName;
    private Integer relatedUserId;



    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

}