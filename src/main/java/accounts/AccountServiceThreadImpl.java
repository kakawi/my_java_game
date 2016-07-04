package accounts;

import dbService.dataSets.UsersDataSet;
import interfaces.AccountServiceThread;
import main.DIC;
import messageSystem.Abonent;
import messageSystem.AddressImpl;
import messageSystem.MessageSystem;

public class AccountServiceThreadImpl implements AccountServiceThread, Runnable, Abonent {
    private final AddressImpl address = new AddressImpl();
    private final MessageSystem messageSystem;
    private final DIC dic;

    @Override
    public MessageSystem getMessageSystem() {
        return  messageSystem;
    }

    @Override
    public AddressImpl getAddress() {
        return address;
    }

    @Override
    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public AccountServiceThreadImpl(MessageSystem messageSystem, DIC dic) {
        this.messageSystem = messageSystem;
        this.dic = dic;
    }

    public UsersDataSet getUserBySessionId(String sessionId) {
        return dic.get(AccountService.class).getUserBySessionId(sessionId);
    }
}
