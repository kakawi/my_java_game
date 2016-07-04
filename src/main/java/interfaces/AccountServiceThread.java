package interfaces;

import dbService.dataSets.UsersDataSet;
import messageSystem.Abonent;
import messageSystem.MessageSystem;

public interface AccountServiceThread extends Runnable, Abonent{

    MessageSystem getMessageSystem();
    UsersDataSet getUserBySessionId(String sessionId);
}
