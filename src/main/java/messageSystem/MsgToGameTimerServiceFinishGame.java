package messageSystem;

import interfaces.GameTimerThread;
import main.Game;

public class MsgToGameTimerServiceFinishGame extends MsgToGameTimerService {
    private final Game game;
    public MsgToGameTimerServiceFinishGame(Address from, Address to, Game game) {
        super(from, to);
        this.game = game;
    }

    @Override
    void exec(GameTimerThread gameTimerThread) {
        gameTimerThread.finishGame(game);
    }
}
