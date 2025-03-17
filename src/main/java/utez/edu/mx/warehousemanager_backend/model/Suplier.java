package utez.edu.mx.warehousemanager_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supliers")
public class Suplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private UUID uuid;
    private String name;
    private String email;
    private LocalDateTime creationDate = LocalDateTime.now();
    private Integer relatedUserId;

    @JsonIgnore
    @OneToMany(mappedBy = "suplier")
    private Set<ProductEntry> productEntries = new HashSet<>();

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

}