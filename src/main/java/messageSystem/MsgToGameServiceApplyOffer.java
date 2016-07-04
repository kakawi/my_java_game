package messageSystem;

import interfaces.GameServiceThread;

import javax.servlet.http.HttpSession;

public class MsgToGameServiceApplyOffer extends MsgToGameService {
    private final HttpSession httpSession;
    public MsgToGameServiceApplyOffer(Address from, Address to, HttpSession session) {
        super(from, to);
        this.httpSession = session;
    }

    void exec(GameServiceThread gameService) {
        gameService.applyOffer(httpSession);
    }
}
