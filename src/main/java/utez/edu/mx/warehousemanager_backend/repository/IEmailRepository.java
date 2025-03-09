package utez.edu.mx.warehousemanager_backend.repository;

import jakarta.mail.MessagingException;
import utez.edu.mx.warehousemanager_backend.model.EmailModel;

public interface IEmailRepository {
    public void sendEmail(EmailModel emailModel) throws MessagingException;
}
