package servlets;

import accounts.AccountService;
import dbService.dataSets.UsersDataSet;
import interfaces.Frontend;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class MainServlet extends HttpServlet {
    private ApplicationContext applicationContext;

    public MainServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();
        String sessionId = req.getSession().getId();

        String flash = req.getParameter("flash");
        pageVariables.put("flash", flash);

        pageVariables.put("SessionID", sessionId);
        AccountService accountService = (AccountService)applicationContext.getBean("accountService");
        UsersDataSet user = accountService.getUserBySessionId(sessionId);
        if (user == null) {
            pageVariables.put("message", "Вы не авторизованы");
        } else {
            pageVariables.put("login", user.getLogin());
            pageVariables.put("profile", user);
        }

        ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/homepage.html", pageVariables);
    }
}
