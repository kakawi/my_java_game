package interfaces;

import messageSystem.Abonent;
import messageSystem.Address;

public interface DBServiceThread extends Runnable, Abonent {

    @Override
    void run();
    Address getAddress();
}
