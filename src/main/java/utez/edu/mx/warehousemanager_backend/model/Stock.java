package utez.edu.mx.warehousemanager_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock")
public class Stock {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", nullable = false)
   private Integer id;
   private UUID uuid;
   private String productName;
   private String mesurementUnit;
   private int quantity;
   @Column(columnDefinition = "DECIMAL(10, 2) DEFAULT 00.00")
   private double unitPrice;
   @Column(columnDefinition = "DECIMAL(10, 2) DEFAULT 00.00")
   private double totalAmount;
   private Integer suplierId;


    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }


}
