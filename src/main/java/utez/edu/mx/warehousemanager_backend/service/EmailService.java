package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import utez.edu.mx.warehousemanager_backend.model.EmailModel;
import utez.edu.mx.warehousemanager_backend.repository.IEmailRepository;

@Slf4j
@Service
public class EmailService implements IEmailRepository {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(EmailModel emailModel) throws MessagingException {
        try {
            log.info("Preparing to send email to {}", emailModel.getRecipient());
            MimeMessage message = javaMailSender.createMimeMessage();
            log.info("MimeMessage created");
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF8");
            log.info("MimeMessageHelper created");

            helper.setTo(emailModel.getRecipient());
            log.info("Recipient set to {}", emailModel.getRecipient());
            helper.setSubject(emailModel.getSubject());
            log.info("Subject set to {}", emailModel.getSubject());

            Context context = new Context();
            log.info("Context variables set");
            context.setVariable("message", emailModel.getMessage());
            context.setVariable("email", emailModel.getEmail());
            context.setVariable("password", emailModel.getPassword());
            String contenHTML = templateEngine.process("email", context);
            log.info("Email content processed");

            helper.setText(contenHTML, true);
            log.info("Email content set");
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", emailModel.getRecipient());

        } catch (MessagingException e) {
            log.error("Failed to send email to {}", emailModel.getRecipient(), e);
            throw new MessagingException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending email to {}", emailModel.getRecipient(), e);
            throw new MessagingException("Unexpected error occurred while sending email", e);
        }
    }
}
