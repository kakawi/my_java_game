package game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import interfaces.GameServiceThread;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;

import javax.servlet.http.HttpSession;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
public class GameWebSocket {
    private final GameServiceThread gameService;
    private Session webSocketSession;
    private final HttpSession httpSession;

    public GameWebSocket(GameServiceThread gameService, ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        this.gameService = gameService;
        httpSession = req.getSession();
    }

    @OnWebSocketConnect
    public void onOpen(Session webSocketSession) {
        this.webSocketSession = webSocketSession;
        gameService.add(this, httpSession);
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(JSRequest.class, new JSRequestDeserializer())
            .create();
        JSRequest jsRequest = gson.fromJson(data, JSRequest.class);
        switch (jsRequest.getAction()) {
            case "get_status":
                gameService.requestToGetStatus(httpSession);
                break;
            case "apply_offer":
                gameService.requestToApplyOffer(httpSession);
                break;
            case "get_games":
                gameService.requestToGetGameOffers(httpSession);
                break;
            case "accept_offer":
                gameService.requestToAcceptOffer(httpSession, jsRequest);
                break;
            case "cancel_offer":
                gameService.requestToCancelOffer(httpSession, jsRequest);
                break;
            case "increase_click_count":
                gameService.requestToIncreaseClickCount(httpSession, jsRequest);
                break;
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        gameService.remove(httpSession);
    }

    public void sendString(String data) {
        try {
            webSocketSession.getRemote().sendString(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
