package messageSystem;

import interfaces.DBServiceThread;

public abstract class MsgToDBService extends Msg{
    public MsgToDBService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if(abonent instanceof DBServiceThread) {
            exec((DBServiceThread) abonent);
        }
    }

    abstract void exec(DBServiceThread dbServiceThread);
}
