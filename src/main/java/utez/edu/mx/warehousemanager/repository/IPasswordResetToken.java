package utez.edu.mx.warehousemanager.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import utez.edu.mx.warehousemanager.model.ResetTokenModel;
import utez.edu.mx.warehousemanager.model.UserModel;

public interface IPasswordResetToken extends JpaRepository<ResetTokenModel, Long> {
    ResetTokenModel findByToken(String token);

    ResetTokenModel findByUserAndExpiryDateAfter(UserModel user, LocalDateTime now);
}
