package servlets;

import accounts.AccountService;
import dbService.dataSets.UsersDataSet;
import interfaces.Frontend;
import main.DIC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class ArenaServlet extends HttpServlet {
    private DIC dic;

    public ArenaServlet(DIC dic) {
        this.dic = dic;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();
        String sessionId = req.getSession().getId();

        String flash = req.getParameter("flash");
        pageVariables.put("flash", flash);

        pageVariables.put("SessionID", sessionId);
        AccountService accountService = dic.get(AccountService.class);
//        UsersDataSet user = accountService.getUserBySessionId(sessionId);
        UsersDataSet user = (UsersDataSet)req.getSession().getAttribute("profile");
//        Map<Long, Game> games = dic.get(GameServiceThread.class).getGameOffers();

//        pageVariables.put("games", games);
        if (user == null) {
            response.sendRedirect("/homepage");
        } else {
            dic.get(Frontend.class).showPage(response, "main/arena.html", pageVariables);
        }
    }
}
