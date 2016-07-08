package services;

import interfaces.GameServiceThread;
import interfaces.GameTimerThread;
import main.Game;
import messageSystem.*;

import java.util.Date;

public class GameTimerThreadImpl implements GameTimerThread {
    private final Address address = new AddressImpl();
    private final MessageSystem messageSystem;
    private boolean sleepNextIteration = true;

    public GameTimerThreadImpl(MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
    }

    @Override
    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            try {
                if (sleepNextIteration) {
                    Thread.sleep(100);
                } else {
                    sleepNextIteration = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finishGame(Game game) {
        Long timeEnd = game.getTimeEnd();
        Date now = new Date();
        try {
            System.out.println(timeEnd - now.getTime());
            Thread.sleep(timeEnd - now.getTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sleepNextIteration = false;
        Address gameServiceAddress = messageSystem.getAddressService().getServiceAddress(GameServiceThread.class);
        Msg message = new MsgToGameServiceFinishGame(getAddress(), gameServiceAddress, game);
        messageSystem.sendMessage(message);
    }

    @Override
    public Address getAddress() {
        return address;
    }
}
