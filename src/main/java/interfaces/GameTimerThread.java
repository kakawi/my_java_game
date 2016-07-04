package interfaces;

import main.Game;
import messageSystem.Abonent;

public interface GameTimerThread extends Runnable, Abonent{
    void finishGame(Game game);
}
