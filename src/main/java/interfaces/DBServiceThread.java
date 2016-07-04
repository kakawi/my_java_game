package interfaces;

import messageSystem.Address;

public interface DBServiceThread extends Address, Runnable {

    @Override
    void run();
    Address getAddress();
}
