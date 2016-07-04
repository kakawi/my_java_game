package messageSystem;

import game.GameServiceImpl;
import interfaces.GameServiceThread;

public abstract class MsgToGameService extends Msg{
    public MsgToGameService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if(abonent instanceof GameServiceImpl) {
//            try {
//                Thread.sleep(1000); // delay for debug
                exec((GameServiceThread) abonent);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    abstract void exec(GameServiceThread gameService);
}
