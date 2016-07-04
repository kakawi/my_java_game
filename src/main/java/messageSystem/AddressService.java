package messageSystem;

import interfaces.AccountServiceThread;
import interfaces.DBServiceThread;
import interfaces.GameServiceThread;
import interfaces.GameTimerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddressService {
    private final List<Address> dbServices = new ArrayList<>();
    private final List<Address> accountServices = new ArrayList<>();
    private final List<Address> gameServices = new ArrayList<>();
    private final List<Address> gameTimerServices = new ArrayList<>();

    private final AtomicInteger asCounter = new AtomicInteger();

    public Address getDBService() {
        final int index = asCounter.incrementAndGet() % dbServices.size();
        return dbServices.get(index);
    }

    public Address getAccountService() {
        final int index = asCounter.incrementAndGet() % accountServices.size();
        return accountServices.get(index);
    }

    public Address getGameService() {
        final int index = asCounter.incrementAndGet() % gameServices.size();
        return gameServices.get(index);
    }

    public Address getGameTimerService() {
        final int index = asCounter.incrementAndGet() % gameTimerServices.size();
        return gameTimerServices.get(index);
    }

    public void addDBService(DBServiceThread dbService) {
        dbServices.add(dbService.getAddress());
    }

    public void addAccountService(AccountServiceThread accountServiceThread) {
        accountServices.add(accountServiceThread.getAddress());
    }

    public void addGameService(GameServiceThread gameServiceThread) {
        gameServices.add(gameServiceThread.getAddress());
    }

    public void addGameTimerService(GameTimerThread gameTimerThread) {
        gameTimerServices.add(gameTimerThread.getAddress());
    }


}
