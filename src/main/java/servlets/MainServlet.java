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

public class MainServlet extends HttpServlet {
    private ApplicationContext applicationContext;

    public MainServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();

        String flash = req.getParameter("message");
        pageVariables.put("message", flash);

        UsersDataSet user = (UsersDataSet)req.getSession().getAttribute("profile");
        if (user == null) {
            pageVariables.put("message", "You're not authorized");
        } else {
            pageVariables.put("login", user.getLogin());
            pageVariables.put("profile", user);
        }

        ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/homepage.html", pageVariables);
    }
}
