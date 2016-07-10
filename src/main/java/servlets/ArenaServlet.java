package servlets;

import dbService.dataSets.UsersDataSet;
import interfaces.Frontend;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class ArenaServlet extends HttpServlet {
    private ApplicationContext applicationContext;

    public ArenaServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();
        String sessionId = req.getSession().getId();

        String flash = req.getParameter("flash");
        pageVariables.put("flash", flash);

        pageVariables.put("SessionID", sessionId);
        UsersDataSet user = (UsersDataSet)req.getSession().getAttribute("profile");

        if (user == null) {
            response.sendRedirect("/homepage");
        } else {
            ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/arena.html", pageVariables);
        }
    }
}
