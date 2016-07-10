package messageSystem;

import dbService.dataSets.UsersDataSet;
import interfaces.DBServiceThread;

import javax.servlet.http.HttpSession;

public class MsgToDBServiceGetUserData extends MsgToDBService {
    private final HttpSession httpSession;

    public MsgToDBServiceGetUserData(Address from, Address to, HttpSession session) {
        super(from, to);
        this.httpSession = session;
    }

    void exec(DBServiceThread dbServiceThread) {
        UsersDataSet user = (UsersDataSet)httpSession.getAttribute("profile");
        String login = user.getLogin();

        try {
            user = dbServiceThread.getUserByLogin(login);
            if(user != null) {

                Msg message = new MsgToGameServiceSendUserData(getTo(), getFrom(), httpSession, user);
                dbServiceThread.getMessageSystem().sendMessage(message);
                getFrom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
