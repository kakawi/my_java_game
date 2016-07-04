package messageSystem;

import interfaces.GameServiceThread;

import javax.servlet.http.HttpSession;

public class MsgToGameServiceAcceptOffer extends MsgToGameService {
    private final HttpSession httpSession;
    private final Long gameSessionId;
    public MsgToGameServiceAcceptOffer(Address from, Address to, HttpSession session, Long gameSessionId) {
        super(from, to);
        this.httpSession = session;
        this.gameSessionId = gameSessionId;
    }

    void exec(GameServiceThread gameService) {
        gameService.acceptOffer(httpSession, gameSessionId);
    }
}
