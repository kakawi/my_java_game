package servlets;

import accounts.AccountService;
import dbService.DBException;
import dbService.dataSets.UsersDataSet;
import interfaces.DBService;
import interfaces.Frontend;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class SignUpServlet extends HttpServlet {
    private ApplicationContext applicationContext;

    public SignUpServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/signup.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        DBService dbService = (DBService) applicationContext.getBean("dbService");
        try {
            UsersDataSet user = dbService.getUserByLogin(login);
            if (user != null) {
                HashMap<String, Object> pageVariables = new HashMap<>();
                pageVariables.put("message", "The user with login: <b>" + user.getLogin() + "</b> have registered");
                ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/signup.html", pageVariables);
                return;
            }

        } catch (DBException e) {
            e.printStackTrace();
        }

        UsersDataSet user = new UsersDataSet(login, password, email);
        try {
            AccountService accountService = ((AccountService)applicationContext.getBean("accountService"));
            accountService.addNewUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("/homepage?flash=You're register");
    }
}
