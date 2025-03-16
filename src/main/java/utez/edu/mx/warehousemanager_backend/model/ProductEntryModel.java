package utez.edu.mx.warehousemanager_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "product_entries")
public class ProductEntryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "product_name", nullable = false, length = 64)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryModel category;

    @Column(name = "msurement_unit", length = 32)
    private String msurementUnit;

    @Column(name = "quantity")
    private Integer quantity;

    @ColumnDefault("0.00")
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @ColumnDefault("0.00")
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "entry_date")
    private Instant entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suplier_id")
    private utez.edu.mx.warehousemanager_backend.model.SuplierModel suplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id")
    private UserModel relatedUser;

}