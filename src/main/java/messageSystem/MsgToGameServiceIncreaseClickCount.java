package messageSystem;

import interfaces.GameServiceThread;

import javax.servlet.http.HttpSession;

public class MsgToGameServiceIncreaseClickCount extends MsgToGameService {
    private final HttpSession httpSession;
    public MsgToGameServiceIncreaseClickCount(Address from, Address to, HttpSession session) {
        super(from, to);
        this.httpSession = session;
    }

    void exec(GameServiceThread gameService) {
        gameService.increaseClickCount(httpSession);
    }
}
