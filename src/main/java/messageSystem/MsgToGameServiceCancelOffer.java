package messageSystem;

import interfaces.GameServiceThread;

import javax.servlet.http.HttpSession;

public class MsgToGameServiceCancelOffer extends MsgToGameService {
    private final HttpSession httpSession;
    public MsgToGameServiceCancelOffer(Address from, Address to, HttpSession session) {
        super(from, to);
        this.httpSession = session;
    }

    void exec(GameServiceThread gameService) {
        gameService.cancelOffer(httpSession);
    }
}
