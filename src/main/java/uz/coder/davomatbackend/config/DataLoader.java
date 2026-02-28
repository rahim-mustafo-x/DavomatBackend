package uz.coder.davomatbackend.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.db.model.UserDbModel;
import static uz.coder.davomatbackend.todo.Strings.ROLE_ADMIN;
import static uz.coder.davomatbackend.todo.Strings.ROLE_STUDENT;
import static uz.coder.davomatbackend.todo.Strings.ROLE_TEACHER;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserDatabase userDatabase;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // ⚠️ SECURITY WARNING: These are default development credentials
            // Change these passwords immediately in production!
            
            // Check if admin exists
            if (userDatabase.findByEmail("admin@davomat.uz") == null) {
                UserDbModel admin = new UserDbModel(
                        "Admin",
                        "User",
                        "admin@davomat.uz",
                        passwordEncoder.encode("admin123"),
                        "+998901234567",
                        ROLE_ADMIN,
                        null,
                        Instant.now()
                );
                userDatabase.save(admin);
                System.out.println("✅ Admin user created: admin@davomat.uz / admin123");
                System.out.println("⚠️  WARNING: Change this password immediately!");
            }

            // Check if teacher exists
            if (userDatabase.findByEmail("teacher@davomat.uz") == null) {
                UserDbModel teacher = new UserDbModel(
                        "Teacher",
                        "User",
                        "teacher@davomat.uz",
                        passwordEncoder.encode("teacher123"),
                        "+998901234568",
                        ROLE_TEACHER,
                        null,
                        Instant.now()
                );
                userDatabase.save(teacher);
                System.out.println("✅ Teacher user created: teacher@davomat.uz / teacher123");
            }

            // Check if student exists
            if (userDatabase.findByEmail("student@davomat.uz") == null) {
                UserDbModel student = new UserDbModel(
                        "Student",
                        "User",
                        "student@davomat.uz",
                        passwordEncoder.encode("student123"),
                        "+998901234569",
                        ROLE_STUDENT,
                        null,
                        Instant.now()
                );
                userDatabase.save(student);
                System.out.println("✅ Student user created: student@davomat.uz / student123");
            }
        };
    }
}
