package accounts;

import dbService.DBException;
import dbService.dataSets.UsersDataSet;
import interfaces.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class AccountService {
//    private final Map<String, UserProfile> loginToProfile;
    private final Map<String, UsersDataSet> sessionIdToProfile;

    @Autowired
    private ApplicationContext applicationContext;

    public AccountService() {
        sessionIdToProfile = new HashMap<>();
    }

    public void addNewUser(UsersDataSet user) throws DBException {
        ((DBService)applicationContext.getBean("dbService")).addUser(user);
    }

    public UsersDataSet getUserByLogin(String login) throws DBException{
        return ((DBService)applicationContext.getBean("dbService")).getUserByLogin(login);
    }

    public UsersDataSet getUserBySessionId(String sessionId) {
        return sessionIdToProfile.get(sessionId);
    }

    public void addSession(String sessionId, UsersDataSet user) {
        sessionIdToProfile.put(sessionId, user);
    }

    public void deleteSession(String sessionId) {
        sessionIdToProfile.remove(sessionId);
    }
}
