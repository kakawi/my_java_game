package main;

import dbService.dataSets.UsersDataSet;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private static AtomicInteger gameSessionIdCreator = new AtomicInteger();
    private final long gameSessionId;
    private UsersDataSet player1;
    private String player1SessionId;
    private UsersDataSet player2;
    private String player2SessionId;
    private boolean isStarted = false;

    private Long timeEnd;
    private int clickCount1;
    private int clickCount2;
    private Winner winner;

    private final int timeForGame = 15; // second

    public boolean isStarted() {
        return isStarted;
    }

    public int increaseClickCountPlayer1() {
        return ++clickCount1;
    }

    public int increaseClickCountPlayer2() {
        return ++clickCount2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return gameSessionId == game.gameSessionId;

    }

    public long getGameSessionId() {
        return gameSessionId;
    }

    @Override
    public int hashCode() {
        return (int) (gameSessionId ^ (gameSessionId >>> 32));
    }

    public Game(UsersDataSet player1, String httpSessionId) {
        this.player1 = player1;
        this.player1SessionId = httpSessionId;
        gameSessionId = gameSessionIdCreator.incrementAndGet();
    }

    public void startGame() {
        // 1. Вычислить время окончания игры (now + 30 сек)
        Date date = new Date();
        Long now = date.getTime();
        // 2. Закинуть в очередь к GameTimer сообщение, передав саму игру (он по очереди будет проверять)
        setTimeEnd(now + timeForGame * 1000);

        isStarted = true; // game has started
    }

    public void finishGame() {
        // 1. Подсчет результатов
        if(clickCount1 > clickCount2) {
            winner = Winner.FIRST;
        } else if (clickCount1 < clickCount2) {
            winner = Winner.SECOND;
        } else {
            winner = Winner.TIE;
        }
    }

    public Winner getWinner() {
        return winner;
    }

    public UsersDataSet getPlayer1() {
        return player1;
    }

    public void setPlayer1(UsersDataSet player1) {
        this.player1 = player1;
    }

    public UsersDataSet getPlayer2() {
        return player2;
    }

    public void setPlayer2(UsersDataSet player2, HttpSession session) {
        this.player2 = player2;
        this.player2SessionId = session.getId();
    }

    public String getPlayer1SessionId() {
        return player1SessionId;
    }

    public String getPlayer2SessionId() {
        return player2SessionId;
    }

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getClickCount1() {
        return clickCount1;
    }

    public int getClickCount2() {
        return clickCount2;
    }

    public enum Winner {FIRST, SECOND, TIE}
}
