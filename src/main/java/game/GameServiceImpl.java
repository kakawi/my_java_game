package game;

import com.google.gson.Gson;
import dbService.dataSets.UsersDataSet;
import interfaces.GameServiceThread;
import main.Game;
import messageSystem.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServiceImpl implements GameServiceThread {
//    private Set<GameWebSocket> webSockets;
    private final MessageSystem messageSystem;
    private final Address address = new AddressImpl();
    private Map<String, GameWebSocket> webSockets = new HashMap<>(); // <httpSessionId, GameWebSocket>
    private Map<String, Game> playerInGame = new ConcurrentHashMap<>(); // <httpSessionId, Game>
    private Map<String, Game> gameOffers = new ConcurrentHashMap<>(); // <httpSessionId, Game> synchronized
    private Map<Long, Game> listOfGames = new ConcurrentHashMap<>(); // <gameSessionId, Game> synchronized

    public void getGameOffers(String httpSessionId) {
        Map<String, Object> answer = getActionStatus("get_games", "done");

        List<Map<String, String>> gamesForJson = new ArrayList<>();

        for (Map.Entry<String, Game> entry : gameOffers.entrySet()) { // <httpSessionId, Game>
            if(httpSessionId.equals(entry.getKey())) continue; // skip if this game belongs to the player

            Map<String, String> gameForJson = new HashMap<>();
            gameForJson.put("gameSessionId", String.valueOf(entry.getValue().getGameSessionId()));
            gameForJson.put("login", entry.getValue().getPlayer1().getLogin());
            gamesForJson.add(gameForJson);
        }

        answer.put("games", gamesForJson);

        Gson gson = new Gson();
        sendMessage(httpSessionId, gson.toJson(answer));
    }

    private void sendGameOffersForEverybody() {
        for (Map.Entry<String, GameWebSocket> entry : webSockets.entrySet()) {
            getGameOffers(entry.getKey());
        }
    }

    public void requestToGetGameOffers(HttpSession session) {
        // send answer
        Map<String, Object> answer = getActionStatus("get_games", "wait");
        Gson gson = new Gson();
        sendMessage(session.getId(), gson.toJson(answer));

        // send the message
        Msg message = new MsgToGameServiceGetGames(getAddress(), getAddress(), session.getId());
        messageSystem.sendMessage(message);
    }

    public void requestToAcceptOffer(HttpSession session, JSRequest jsRequest) {
        Long gameSessionId = jsRequest.getData().get("gameSessionId").getAsLong();
        // send the message
        Msg message = new MsgToGameServiceAcceptOffer(getAddress(), getAddress(), session, gameSessionId);
        messageSystem.sendMessage(message);
    }

    public void requestToCancelOffer(HttpSession session, JSRequest jsRequest) {
        // send the message
        Msg message = new MsgToGameServiceCancelOffer(getAddress(), getAddress(), session);
        messageSystem.sendMessage(message);
    }

    public void requestToIncreaseClickCount(HttpSession session, JSRequest jsRequest) {
        // send the message
        Msg message = new MsgToGameServiceIncreaseClickCount(getAddress(), getAddress(), session);
        messageSystem.sendMessage(message);
    }

    public void acceptOffer(HttpSession session, Long gameSessionId) {
        Game game = listOfGames.get(gameSessionId);
        if (game.getGameSessionId() == gameSessionId) {
            gameOffers.remove(game.getPlayer1SessionId());

            UsersDataSet player2 = (UsersDataSet)session.getAttribute("profile");
            game.setPlayer2(player2, session);
            startGame(game);
        }
        sendGameOffersForEverybody();
    }

    public void cancelOffer(HttpSession session) {
        Game game = gameOffers.get(session.getId());
        if (game != null) {
            gameOffers.remove(session.getId());
            // send messages
            Map<String, Object> answer = getActionStatus("cancel_offer", "done");
            Gson gson = new Gson();
            sendMessage(session.getId(), gson.toJson(answer));
        } else {
            System.out.println("Игра не найдена");
        }

        sendGameOffersForEverybody();
    }

    public void increaseClickCount(HttpSession session) {
        String httpSessionId = session.getId();
        Game game = playerInGame.get(httpSessionId);
        if (game != null) {
            if (game.getPlayer1SessionId().equals(httpSessionId)) {
                game.increaseClickCountPlayer1();
            } else if (game.getPlayer2SessionId().equals(httpSessionId)) {
                game.increaseClickCountPlayer2();
            }

            // send messages
            Map<String, Object> answer = getActionStatus("increase_click_count", "done");
            Gson gson = new Gson();
            // For Player # 1
            answer.put("click_count_player", game.getClickCount1());
            answer.put("click_count_enemy", game.getClickCount2());
            sendMessage(game.getPlayer1SessionId(), gson.toJson(answer));

            // For Player # 2
            answer.put("click_count_player", game.getClickCount2());
            answer.put("click_count_enemy", game.getClickCount1());
            sendMessage(game.getPlayer2SessionId(), gson.toJson(answer));
        } else {
            System.out.println("Игра не найдена");
        }
    }

    private void startGame(Game game) {
        game.startGame();
        playerInGame.put(game.getPlayer1SessionId(), game);
        playerInGame.put(game.getPlayer2SessionId(), game);

        Address gameTimerAddress = messageSystem.getAddressService().getGameTimerService();

        // send message for GameTimerService
        Msg message = new MsgToGameTimerServiceFinishGame(getAddress(), gameTimerAddress, game);
        messageSystem.sendMessage(message);

        // send messages for 2 players
        Map<String, Object> answer = getActionStatus("start_game", "done");
        Gson gson = new Gson();

        String player1SessionId = game.getPlayer1SessionId();
        String player2SessionId = game.getPlayer2SessionId();
        sendMessage(player1SessionId, gson.toJson(answer));
        sendMessage(player2SessionId, gson.toJson(answer));
    }

    public void finishGame(Game game) {
        game.finishGame();
        listOfGames.remove(game.getGameSessionId());
        String httpSessionId1 = game.getPlayer1SessionId();
        String httpSessionId2 = game.getPlayer2SessionId();

        // send messages for 2 players
        Map<String, Object> answer = getActionStatus("finish_game", "done");

        Gson gson = new Gson();
        answer.put("score_player", game.getClickCount1());
        answer.put("score_enemy", game.getClickCount2());
        String player1SessionId = game.getPlayer1SessionId();
        sendMessage(player1SessionId, gson.toJson(answer));
        playerInGame.remove(httpSessionId1);

        answer.put("score_player", game.getClickCount2());
        answer.put("score_enemy", game.getClickCount1());
        String player2SessionId = game.getPlayer2SessionId();
        sendMessage(player2SessionId, gson.toJson(answer));
        playerInGame.remove(httpSessionId2);
    }


    public GameServiceImpl(MessageSystem messageSystem) {
        this.messageSystem = messageSystem;
    }

    public void sendMessage(String sessionId, String data) {
        GameWebSocket webSocket = webSockets.get(sessionId);
        try {
            webSocket.sendString(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void add(GameWebSocket webSocket, HttpSession session) {
        webSockets.put(session.getId(), webSocket);
//        webSockets.add(webSocket);
    }

    public void requestToGetStatus(HttpSession session) {
        Map<String, Object> answer = getActionStatus("get_status", "done");

        String httpSessionId = session.getId();
        if(gameOffers.containsKey(httpSessionId)) {
            answer.put("player_status", "apply");
        } else if (playerInGame.containsKey(httpSessionId)) {
            answer.put("player_status", "inGame");
        } else {
            answer.put("player_status", "default");
        }

        // send answer
        Gson gson = new Gson();
        sendMessage(session.getId(), gson.toJson(answer));
    }

    public void requestToApplyOffer(HttpSession session) {
        // Check if this player has a game
        if (gameOffers.containsKey(session.getId())) {
            Gson gson = new Gson();
            sendMessage(session.getId(), gson.toJson("У вас уже есть заявка"));
            return;
        }

        if (playerInGame.containsKey(session.getId())) {
            Gson gson = new Gson();
            sendMessage(session.getId(), gson.toJson("Вы уже в игре"));
            return;
        }

        // send the message
        Msg message = new MsgToGameServiceApplyOffer(getAddress(), getAddress(), session);
        messageSystem.sendMessage(message);

        // send answer
//        Map<String, Object> answer = getActionStatus("apply_offer", "wait");
//        Gson gson = new Gson();
//        sendMessage(session.getId(), gson.toJson(answer));
    }

    private Map<String, Object> getActionStatus(String action, String status) {
        Map<String, Object> answer = new HashMap<>();
        answer.put("action", action);
        answer.put("status", status);

        return answer;
    }

    public void applyOffer(HttpSession session) {
        UsersDataSet player1 = (UsersDataSet)session.getAttribute("profile");
        String sessionId = session.getId();
        Game game = new Game(player1, sessionId);
        gameOffers.put(sessionId, game);
        listOfGames.put(game.getGameSessionId(), game);

        Map<String, Object> answer = getActionStatus("apply_offer", "created");
        answer.put("gameSessionId", String.valueOf(game.getGameSessionId()));

        Gson gson = new Gson();
        sendMessage(sessionId, gson.toJson(answer));

        sendGameOffersForEverybody();
    }

    public void remove(HttpSession session) {
        webSockets.remove(session.getId());
    }

    @Override
    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public MessageSystem getMessageSystem() {
        return messageSystem;
    }
}
