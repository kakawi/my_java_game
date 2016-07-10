package messageSystem;

import dbService.dataSets.UsersDataSet;
import interfaces.GameServiceThread;

import javax.servlet.http.HttpSession;

public class MsgToGameServiceSendUserData extends MsgToGameService {
    private final UsersDataSet user;
    private final HttpSession httpSession;
    public MsgToGameServiceSendUserData(Address from, Address to, HttpSession httpSession, UsersDataSet user) {
        super(from, to);
        this.user = user;
        this.httpSession = httpSession;
    }

    void exec(GameServiceThread gameService) {
        gameService.sendUserData(httpSession, user);
    }
}
