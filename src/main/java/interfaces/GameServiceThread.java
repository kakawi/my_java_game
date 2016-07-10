package interfaces;

import dbService.dataSets.UsersDataSet;
import game.GameWebSocket;
import game.JSRequest;
import main.Game;
import messageSystem.Abonent;
import messageSystem.MessageSystem;

import javax.servlet.http.HttpSession;

public interface GameServiceThread extends Runnable, Abonent{
    MessageSystem getMessageSystem();

    void requestToGetStatus(HttpSession session);
    void requestToApplyOffer(HttpSession session);
    void requestToGetGameOffers(HttpSession session);
    void requestToAcceptOffer(HttpSession session, JSRequest jsRequest);
    void requestToCancelOffer(HttpSession session, JSRequest jsRequest);
    void requestToIncreaseClickCount(HttpSession session, JSRequest jsRequest);
    void add(GameWebSocket webSocket, HttpSession session);
    void sendMessage(String sessionId, String data);
    void remove(HttpSession session);
    void applyOffer(HttpSession session);
    void acceptOffer(HttpSession session, Long gameSessionId);
    void cancelOffer(HttpSession session);
    void increaseClickCount(HttpSession session);
    void getGameOffers(String httpSessionId);
    void finishGame(Game game);
    void sendUserData(HttpSession session, UsersDataSet user);
}
