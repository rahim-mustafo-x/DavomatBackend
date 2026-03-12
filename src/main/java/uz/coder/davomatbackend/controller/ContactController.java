package uz.coder.davomatbackend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uz.coder.davomatbackend.service.EmailService;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EmailService emailService;

    // application.properties dan oladi
    @Value("${spring.mail.username}")
    private String toEmail;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody Map<String, String> data) {
        try {
            String emailText = "Ism: " + data.get("name") +
                    "\nEmail: " + data.get("email") +
                    "\nXabar: " + data.get("message");

            emailService.sendEmail(
                    toEmail,  // propertiesdan olingan
                    "Davomat App Contact Form",
                    emailText
            );

            return ResponseEntity.ok("Xabaringiz yuborildi!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Serverda xatolik yuz berdi: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEmail() {
        try {
            emailService.sendEmail(
                    toEmail,
                    "Test Email - Davomat App",
                    "Bu test xabar. Agar bu xabarni ko'rsangiz, email konfiguratsiyasi to'g'ri ishlayapti!"
            );
            return ResponseEntity.ok("Test email yuborildi: " + toEmail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Email yuborishda xatolik: " + e.getMessage());
        }
    }
}
