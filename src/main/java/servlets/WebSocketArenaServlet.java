package servlets;

import game.GameWebSocket;
import interfaces.GameServiceThread;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/game"})
public class WebSocketArenaServlet extends WebSocketServlet {
    private final static int LOGOUT_TIME = 10 * 60 * 1000;
    private final GameServiceThread gameService;

    public WebSocketArenaServlet(GameServiceThread gameServiceThread) {
        this.gameService = gameServiceThread;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(LOGOUT_TIME);
        factory.setCreator((req, resp) -> new GameWebSocket(gameService, req, resp));
    }
}
