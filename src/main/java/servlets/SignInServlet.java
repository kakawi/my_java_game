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

public class SignInServlet extends HttpServlet {
    private ApplicationContext applicationContext;

    public SignInServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/signin.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (login == null || password == null) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            AccountService accountService = (AccountService)applicationContext.getBean("accountService");
            UsersDataSet user = accountService.getUserByLogin(login);

            if (user == null || !user.getPassword().equals(password)) {
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                HashMap<String, Object> pageVariables = new HashMap<>();
                pageVariables.put("login", login);
                pageVariables.put("message", "Login/password is incorrect");
                ((Frontend)applicationContext.getBean("frontend")).showPage(response, "main/signin.html", pageVariables);
                return;
            }

            req.getSession().setAttribute("profile", user);
            response.sendRedirect("/homepage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
