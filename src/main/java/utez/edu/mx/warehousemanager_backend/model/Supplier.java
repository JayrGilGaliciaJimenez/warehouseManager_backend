package utez.edu.mx.warehousemanager_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private UUID uuid;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;
    private LocalDateTime creationDate = LocalDateTime.now();
    private Integer relatedUserId;

    @OneToMany(mappedBy = "supplier")
    @JsonIgnore
    private Set<ProductEntry> productEntries = new HashSet<>();

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

}