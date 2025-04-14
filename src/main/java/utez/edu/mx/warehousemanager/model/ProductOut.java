package utez.edu.mx.warehousemanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private String receiverName;
    private UUID relatedUserUUID;


    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

}