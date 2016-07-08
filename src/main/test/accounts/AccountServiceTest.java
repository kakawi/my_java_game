package accounts;

import dbService.DBException;
import dbService.dataSets.UsersDataSet;
import interfaces.DBService;
import main.DIC;
import org.junit.Test;

public class AccountServiceTest {

    @Test
    public void testAddNewUser() throws Exception {
        AccountService accountService = new AccountService();
        DIC dic = new DIC();
        final DBService dbService = new DBService() {
            @Override
            public UsersDataSet getUser(long id) throws DBException {
                return null;
            }

            @Override
            public UsersDataSet getUserByLogin(String login) throws DBException {
                return null;
            }

            @Override
            public long addUser(String name) throws DBException {
                return 0;
            }

            @Override
            public long addUser(UsersDataSet user) throws DBException {
                return 0;
            }

            @Override
            public void printConnectInfo() {

            }
        };

//        final DBService spyDBService = spy(dbService);
//
//        dic.add(DBService.class, spyDBService);
//        accountService.setDIC(dic);
//        UsersDataSet usersDataSet = new UsersDataSet("testLogin", "testPassword", "test@email.ru");
//
//        doReturn(1L).when(spyDBService).addUser(usersDataSet);
//
//        accountService.addNewUser(usersDataSet);
//        verify(spyDBService, times(1)).addUser(usersDataSet);
    }

    @Test
    public void testGetUserByLogin() throws Exception {

    }
}