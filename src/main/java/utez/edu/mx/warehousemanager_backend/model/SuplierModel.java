package utez.edu.mx.warehousemanager_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "supliers")
public class SuplierModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", length = 64)
    private String email;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "creation_date")
    private Instant creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id")
    private UserModel relatedUser;

    @OneToMany(mappedBy = "suplier")
    private Set<ProductEntryModel> productEntries = new LinkedHashSet<>();

    @OneToMany(mappedBy = "suplier")
    private Set<StockModel> stocks = new LinkedHashSet<>();

}