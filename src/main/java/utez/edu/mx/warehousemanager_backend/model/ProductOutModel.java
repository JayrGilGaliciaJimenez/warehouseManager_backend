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
@Table(name = "product_outs")
public class ProductOutModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "product_name", length = 64)
    private String productName;

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
    @Column(name = "out_date")
    private Instant outDate;

    @Column(name = "receiver_name", length = 32)
    private String receiverName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id")
    private UserModel relatedUser;

}