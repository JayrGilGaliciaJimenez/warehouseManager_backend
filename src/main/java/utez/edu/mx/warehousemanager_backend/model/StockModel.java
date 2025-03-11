package utez.edu.mx.warehousemanager_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "stock")
public class StockModel {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suplier_id")
    private utez.edu.mx.warehousemanager_backend.model.SuplierModel suplier;

}