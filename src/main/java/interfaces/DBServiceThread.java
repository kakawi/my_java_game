package interfaces;

import dbService.DBException;
import dbService.dataSets.UsersDataSet;
import messageSystem.Abonent;
import messageSystem.Address;
import messageSystem.MessageSystem;

public interface DBServiceThread extends Runnable, Abonent {
    MessageSystem getMessageSystem();

    void update(UsersDataSet usersDataSet) throws DBException;

    UsersDataSet getUserByLogin(String login) throws DBException;

    @Override
    void run();
    Address getAddress();
}
