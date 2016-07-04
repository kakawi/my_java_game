package messageSystem;

import interfaces.AccountServiceThread;

public abstract class MsgToAccountService extends Msg{
    public MsgToAccountService(Address from, Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if(abonent instanceof AccountServiceThread) {
            exec((AccountServiceThread) abonent);
        }
    }

    abstract void exec(AccountServiceThread accountServiceThread);
}
