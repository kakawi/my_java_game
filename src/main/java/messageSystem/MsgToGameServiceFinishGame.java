package messageSystem;

import interfaces.GameServiceThread;
import main.Game;

public class MsgToGameServiceFinishGame extends MsgToGameService {
    private final Game game;
    public MsgToGameServiceFinishGame(Address from, Address to, Game game) {
        super(from, to);
        this.game = game;
    }

    void exec(GameServiceThread gameService) {
        gameService.finishGame(game);
    }
}
