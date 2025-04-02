package utez.edu.mx.warehousemanager_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "product_entries")
public class ProductEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private UUID uuid;
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    @JsonIgnore
    private Category category;

    private String measurementUnit;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private LocalDateTime entryDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suplierId", nullable = false)
    private Suplier suplier;

    private Integer relatedUserId;

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}