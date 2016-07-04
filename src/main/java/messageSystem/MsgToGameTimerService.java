package messageSystem;

import interfaces.GameTimerThread;

public abstract class MsgToGameTimerService extends Msg{
    public MsgToGameTimerService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if(abonent instanceof GameTimerThread) {
            exec((GameTimerThread) abonent);
        }
    }

    abstract void exec(GameTimerThread gameTimerThread);
}
