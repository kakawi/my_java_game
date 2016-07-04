package game;

import com.google.gson.Gson;
import dbService.dataSets.UsersDataSet;
import interfaces.GameServiceThread;
import main.Game;
import messageSystem.Address;
import messageSystem.AddressService;
import messageSystem.MessageSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(GameServiceImpl.class)
public class GameServiceImplTest {
    @Mock private Map<String, Game> playerInGame = new ConcurrentHashMap<>(); // <httpSessionId, Game>
    @Mock private Map<String, Game> gameOffers = new ConcurrentHashMap<>(); // <httpSessionId, Game> synchronized
    @Mock private Map<Long, Game> listOfGames = new ConcurrentHashMap<>(); // <gameSessionId, Game> synchronized

    // Spies
    private Map<String, Game> spyPlayerInGame = spy(playerInGame);
//    private Map<String, Game> spyGameOffers = spy(gameOffers);
    private Map<Long, Game> spyListOfGames = spy(listOfGames);

    @Mock private HttpSession httpSession1;
    @Mock private MessageSystem messageSystem;
    @Mock private HttpSession httpSession2;

    @InjectMocks
    GameServiceThread gameServiceThread = new GameServiceImpl(messageSystem);
//    GameServiceThread gameServiceThread = PowerMockito.spy(new GameServiceImpl(messageSystem));

    private UsersDataSet player1;
    private UsersDataSet player2;


    @Before
    public void setUp() throws Exception {
        player1 = new UsersDataSet("test1", "password1", "test@test.com");
        player2 = new UsersDataSet("test2", "password2", "test@test.com");
        when(httpSession1.getId()).thenReturn("ID_1");
        when(httpSession2.getId()).thenReturn("ID_2");
        when(httpSession1.getAttribute("profile")).thenReturn(player1);
        when(httpSession2.getAttribute("profile")).thenReturn(player2);
    }

    @Test
    public void testGetGameOffers() throws Exception {
        GameServiceThread gameServiceSpy = spy(gameServiceThread);

        UsersDataSet player_1 = new UsersDataSet("test_1", "test_1", "test_1@test.com");
        UsersDataSet player_2 = new UsersDataSet("test_2", "test_2", "test_2@test.com");
        UsersDataSet player_3 = new UsersDataSet("test_3", "test_3", "test_3@test.com");

        String httpSession_1 = "ID_1";
        String httpSession_2 = "ID_2";
        String httpSession_3 = "ID_3";

        Game game1 = new Game(player_1, httpSession_1);
        Game game2 = new Game(player_2, httpSession_2);
        Game game3 = new Game(player_3, httpSession_3);

        Iterator<Map.Entry<String, Game>> iterator = mock(Iterator.class);
        Set<Map.Entry<String, Game>> entrySet = mock(Set.class);
        Map.Entry<String, Game> entry1 = new Map.Entry<String, Game>() {
            @Override
            public String getKey() {
                return httpSession_1;
            }

            @Override
            public Game getValue() {
                return game1;
            }

            @Override
            public Game setValue(Game value) {
                return null;
            }
        };
        Map.Entry<String, Game> entry2 = new Map.Entry<String, Game>() {
            @Override
            public String getKey() {
                return httpSession_2;
            }

            @Override
            public Game getValue() {
                return game2;
            }

            @Override
            public Game setValue(Game value) {
                return null;
            }
        };
        Map.Entry<String, Game> entry3 = new Map.Entry<String, Game>() {
            @Override
            public String getKey() {
                return httpSession_3;
            }

            @Override
            public Game getValue() {
                return game3;
            }

            @Override
            public Game setValue(Game value) {
                return null;
            }
        };

        when(gameOffers.entrySet()).thenReturn(entrySet);
        mockIterable(entrySet, entry1, entry2, entry3);

        gameServiceSpy.getGameOffers(httpSession_1); // checked method

        List<Map<String, String>> gamesForJson = new ArrayList<>();
        Map<String, String> gameForJson1 = new HashMap<>();
        gameForJson1.put("gameSessionId", String.valueOf(game2.getGameSessionId()));
        gameForJson1.put("login", game2.getPlayer1().getLogin());
        gamesForJson.add(gameForJson1);

        Map<String, String> gameForJson2 = new HashMap<>();
        gameForJson2.put("gameSessionId", String.valueOf(game3.getGameSessionId()));
        gameForJson2.put("login", game3.getPlayer1().getLogin());
        gamesForJson.add(gameForJson2);

        Map<String, Object> answer = new HashMap<>();
        answer.put("action", "get_games");
        answer.put("status", "done");
        answer.put("games", gamesForJson);

        Gson gson = new Gson();
        String json = gson.toJson(answer);

        verify(gameServiceSpy, times(1)).sendMessage(httpSession_1, json);
    }

    public static <T> void mockIterable(Iterable<T> iterable, T... values) {
        Iterator<T> mockIterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(mockIterator);

        if (values.length == 0) {
            when(mockIterator.hasNext()).thenReturn(false);
        } else if (values.length == 1) {
            when(mockIterator.hasNext()).thenReturn(true, false);
            when(mockIterator.next()).thenReturn(values[0]);
        } else {
            // build boolean array for hasNext()
            Boolean[] hasNextResponses = new Boolean[values.length];
            for (int i = 0; i < hasNextResponses.length -1 ; i++) {
                hasNextResponses[i] = true;
            }
            hasNextResponses[hasNextResponses.length - 1] = false;
            when(mockIterator.hasNext()).thenReturn(true, hasNextResponses);
            T[] valuesMinusTheFirst = Arrays.copyOfRange(values, 1, values.length);
            when(mockIterator.next()).thenReturn(values[0], valuesMinusTheFirst);
        }
    }

    @Test
    public void testAcceptOffer() throws Exception {
        UsersDataSet player_1 = new UsersDataSet("test_1", "test_1", "test_1@test.com");
        String httpSessionId_1 = "ID_1";
        Game game = new Game(player_1, httpSessionId_1);
        Long gameSessionId = game.getGameSessionId();
        UsersDataSet player_2 = new UsersDataSet("test_2", "test_2", "test_2@test.com");

        HttpSession httpSession = mock(HttpSession.class);

        String httpSessionId_2 = "ID_2";
        when(httpSession.getId()).thenReturn(httpSessionId_2);
        when(httpSession.getAttribute("profile")).thenReturn(player_2);

        when(listOfGames.get(gameSessionId)).thenReturn(game);

        AddressService addressService = mock(AddressService.class);
        when(messageSystem.getAddressService()).thenReturn(addressService);
        Address address = mock(Address.class);
        when(addressService.getGameTimerService()).thenReturn(address);

        GameServiceThread gameServiceThreadSpy = spy(gameServiceThread);
        gameServiceThreadSpy.acceptOffer(httpSession, gameSessionId); // Checked method

        // startGame
        verify(playerInGame, times(1)).put(httpSessionId_1, game);
        verify(playerInGame, times(1)).put(httpSessionId_2, game);

        // sendMessage
        Map<String, Object> answer = new HashMap<>();
        answer.put("action", "start_game");
        answer.put("status", "done");
        Gson gson = new Gson();
        verify(gameServiceThreadSpy, times(1)).sendMessage(httpSessionId_1, gson.toJson(answer));
        verify(gameServiceThreadSpy, times(1)).sendMessage(httpSessionId_2, gson.toJson(answer));
    }

    @Test
    public void testCancelOffer() throws Exception {

    }

    @Test
    public void testIncreaseClickCount() throws Exception {

    }

    @Test
    public void testFinishGame() throws Exception {

    }

    @Test
    public void testSendMessage() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

    }

    @Test
    public void testApplyOffer() throws Exception {
        GameServiceThread spyGameServiceThread = spy(gameServiceThread);
        spyGameServiceThread.applyOffer(httpSession1);

        verify(gameOffers, times(1)).put(anyString(), any(Game.class));
        verify(listOfGames, times(1)).put(anyLong(), any(Game.class));
    }


    @Test
    public void testRemove() throws Exception {

    }
}