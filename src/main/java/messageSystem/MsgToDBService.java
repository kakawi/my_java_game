package messageSystem;

import interfaces.DBService;

public abstract class MsgToDBService extends Msg{
    public MsgToDBService(AddressImpl from, AddressImpl to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if(abonent instanceof DBService) {
            exec((DBService) abonent);
        }
    }

    abstract void exec(DBService dbService);
}
