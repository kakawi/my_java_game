package messageSystem;

import dbService.dataSets.UsersDataSet;
import interfaces.DBServiceThread;
import main.Game;

public class MsgToDBServiceSaveResult extends MsgToDBService {
    private final Game game;

    public MsgToDBServiceSaveResult(Address from, Address to, Game game) {
        super(from, to);
        this.game = game;
    }

    void exec(DBServiceThread dbServiceThread) {
        UsersDataSet player1 = game.getPlayer1();
        UsersDataSet player2 = game.getPlayer2();

        try {
            dbServiceThread.update(player1);
            dbServiceThread.update(player2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
