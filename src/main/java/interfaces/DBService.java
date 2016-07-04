package interfaces;

import dbService.DBException;
import dbService.dataSets.UsersDataSet;

public interface DBService {
    public UsersDataSet getUser(long id) throws DBException;

    public UsersDataSet getUserByLogin(String login) throws DBException;

    public long addUser(String name) throws DBException;

    public long addUser(UsersDataSet user) throws DBException;

    public void printConnectInfo();
}
