package accounts;

import dbService.DBException;
import dbService.dataSets.UsersDataSet;
import interfaces.DBService;
import interfaces.serviceWithDIC;
import main.DIC;

import java.util.HashMap;
import java.util.Map;

public class AccountService implements serviceWithDIC {
//    private final Map<String, UserProfile> loginToProfile;
    private final Map<String, UsersDataSet> sessionIdToProfile;
    private DBService dbService;

    private DIC dic;

    public void setDIC(DIC dic) {
        this.dic = dic;
        this.dbService = dic.get(DBService.class);
    }

    public AccountService() {
        sessionIdToProfile = new HashMap<>();
    }

    public void addNewUser(UsersDataSet user) throws DBException {
            this.dbService.addUser(user);
    }

    public UsersDataSet getUserByLogin(String login) throws DBException{
        return this.dbService.getUserByLogin(login);
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
