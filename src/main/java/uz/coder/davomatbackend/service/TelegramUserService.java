package uz.coder.davomatbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.*;
import uz.coder.davomatbackend.db.model.*;
import uz.coder.davomatbackend.model.TelegramUser;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TelegramUserService {
    private final TelegramUserDatabase database;
    private final UserDatabase userDatabase;
    @Autowired
    public TelegramUserService(TelegramUserDatabase database, UserDatabase userDatabase) {
        this.database = database;
        this.userDatabase = userDatabase;
    }
    public TelegramUser save(TelegramUser telegramUser){
        if (userDatabase.existsByPhoneNumber(telegramUser.getPhoneNumber())) {
            UserDbModel user = userDatabase.findByPhoneNumber(telegramUser.getPhoneNumber());
            if (exactByIdTelegramUserId(telegramUser.getTelegramUserId())) {
                TelegramUserDbModel model = database.findByTelegramUserId(telegramUser.getTelegramUserId());
                model.setUserId(model.getId());
                model.setFirstName(telegramUser.getFirstName());
                model.setLastName(telegramUser.getLastName());
                model.setPhoneNumber(telegramUser.getPhoneNumber());
                database.save(model);
            }else {
                TelegramUserDbModel model = new  TelegramUserDbModel(user.getId(), telegramUser.getTelegramUserId(),  telegramUser.getFirstName(), telegramUser.getLastName(), telegramUser.getPhoneNumber());
                database.save(model);
            }
        }else {
            if (exactByIdTelegramUserId(telegramUser.getTelegramUserId())) {
                TelegramUserDbModel model = database.findByTelegramUserId(telegramUser.getTelegramUserId());
                model.setFirstName(telegramUser.getFirstName());
                model.setLastName(telegramUser.getLastName());
                model.setPhoneNumber(telegramUser.getPhoneNumber());
                database.save(model);
            }else {
                TelegramUserDbModel model = new  TelegramUserDbModel(telegramUser.getTelegramUserId(),  telegramUser.getFirstName(), telegramUser.getLastName(), telegramUser.getPhoneNumber());
                database.save(model);
            }
        }
        TelegramUserDbModel model = database.findByTelegramUserId(telegramUser.getTelegramUserId());
        return new TelegramUser(model.getId(), model.getUserId(), model.getTelegramUserId(), model.getFirstName(), model.getLastName(), model.getPhoneNumber());
    }
    private boolean exactByIdTelegramUserId(long telegramUserId){
        return database.existsByTelegramUserId(telegramUserId);
    }
    public List<TelegramUser> findAll() {
        return database.findAll().stream().map(model->new TelegramUser(model.getId(), model.getTelegramUserId(), model.getFirstName(), model.getLastName(), model.getPhoneNumber())).collect(Collectors.toList());
    }
    
    public Page<TelegramUser> findAllPaginated(Pageable pageable) {
        return database.findAll(pageable).map(model -> 
            new TelegramUser(model.getId(), model.getUserId(), model.getTelegramUserId(), 
                model.getFirstName(), model.getLastName(), model.getPhoneNumber())
        );
    }
    
    public TelegramUser findByUserId(long userId){
        TelegramUserDbModel model = database.findByUserId(userId);
        return new TelegramUser(model.getId(), model.getUserId(), model.getTelegramUserId(), model.getFirstName(), model.getLastName(), model.getPhoneNumber());
    }
}