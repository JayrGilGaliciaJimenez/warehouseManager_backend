package utez.edu.mx.warehousemanager.repository;

import jakarta.mail.MessagingException;
import utez.edu.mx.warehousemanager.model.EmailModel;

public interface IEmailRepository {
    public void sendEmail(EmailModel emailModel, String templateName) throws MessagingException;
}
