package messageSystem;

import interfaces.GameServiceThread;

public class MsgToGameServiceGetGames extends MsgToGameService {
    private final String httpSessionId;
    public MsgToGameServiceGetGames(Address from, Address to, String sessionId) {
        super(from, to);
        this.httpSessionId = sessionId;
    }

    void exec(GameServiceThread gameService) {
        gameService.getGameOffers(httpSessionId);
    }
}
