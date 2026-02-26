package uz.coder.davomatbackend.service;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import uz.coder.davomatbackend.db.TelegramUserDatabase;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.db.model.TelegramUserDbModel;
import uz.coder.davomatbackend.db.model.UserDbModel;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.User;
import static uz.coder.davomatbackend.todo.Strings.ROLE_STUDENT;
import static uz.coder.davomatbackend.todo.Strings.THERE_IS_NO_SUCH_A_PERSON;
import static uz.coder.davomatbackend.todo.Strings.YOU_ARE_NOT_A_STUDENT;

@Service
public class UserService implements UserDetailsService {

    private final UserDatabase database;
    private final TelegramUserDatabase telegramUserDatabase;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDatabase database,
                       TelegramUserDatabase telegramUserDatabase,
                       PasswordEncoder passwordEncoder) {
        this.database = database;
        this.telegramUserDatabase = telegramUserDatabase;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        LocalDate now = LocalDate.now();
        LocalDate balance;

        if (ROLE_STUDENT.equals(user.getRole())) {
            balance = now.plusWeeks(1);
        } else {
            balance = null;
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        UserDbModel save = database.save(
                new UserDbModel(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        encodedPassword,
                        user.getPhoneNumber(),
                        user.getRole(),
                        balance,
                        Instant.now()
                )
        );

        return new User(
                save.getId(),
                save.getFirstName(),
                save.getLastName(),
                save.getEmail(),
                save.getPassword(),
                save.getPhoneNumber(),
                save.getRole(),
                save.getLastPasswordResetAt(),
                save.getPayedDate()
        );
    }

    public User edit(User user) {
        UserDbModel old = database.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));

        String passwordToSave;

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            passwordToSave = old.getPassword();
        } else {
            passwordToSave = passwordEncoder.encode(user.getPassword());
        }

        database.update(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                passwordToSave,
                user.getPhoneNumber(),
                user.getRole(),
                Instant.now()
        );

        UserDbModel save = database.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));

        return new User(
                save.getId(),
                save.getFirstName(),
                save.getLastName(),
                save.getEmail(),
                save.getPassword(),
                save.getPhoneNumber(),
                save.getRole(),
                save.getLastPasswordResetAt(),
                save.getPayedDate()
        );
    }

    public int deleteById(long id) {
        if (database.existsById(id)) {
            database.deleteById(id);
            return 1;
        } else {
            return 0;
        }
    }

    public User findByPhoneNumber(String phoneNumber) {
        UserDbModel model = database.findByPhoneNumber(phoneNumber);
        if (model == null) {
            return null;
        }

        return new User(
                model.getId(),
                model.getFirstName(),
                model.getLastName(),
                model.getEmail(),
                model.getPassword(),
                model.getPhoneNumber(),
                model.getRole(),
                model.getLastPasswordResetAt(),
                model.getPayedDate()
        );
    }

    public boolean updateBalanceUser(Balance balance) {
        long id = balance.getTelegramUserId();
        LocalDate payDate = balance.getLimit();

        TelegramUserDbModel telegramUserDbModel =
                telegramUserDatabase.findByTelegramUserId(id);

        if (telegramUserDbModel == null) {
            throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON);
        }

        UserDbModel user = database.findById(telegramUserDbModel.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));

        if (!ROLE_STUDENT.equals(user.getRole())) {
            throw new IllegalArgumentException(YOU_ARE_NOT_A_STUDENT);
        }

        int updatedRows = database.updateBalanceUser(payDate, user.getId());
        return updatedRows > 0;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserDbModel model = database.findByEmail(email);

        if (model == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        return new User(
                model.getId(),
                model.getFirstName(),
                model.getLastName(),
                model.getEmail(),
                model.getPassword(),
                model.getPhoneNumber(),
                model.getRole(),
                model.getLastPasswordResetAt(),
                model.getPayedDate()
        );
    }

    public User findByEmail(String email) {
        UserDbModel model = database.findByEmail(email);
        if (model == null) {
            return null;
        }

        return new User(
                model.getId(),
                model.getFirstName(),
                model.getLastName(),
                model.getEmail(),
                model.getPassword(),
                model.getPhoneNumber(),
                model.getRole(),
                model.getLastPasswordResetAt(),
                model.getPayedDate()
        );
    }

    public long countAllUsers() {
        return database.count();
    }
}
