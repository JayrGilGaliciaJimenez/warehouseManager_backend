package utez.edu.mx.warehousemanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "access_log")
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String email;
    @Column(name = "accessDate", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime accessDate;
    private Boolean success;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}
