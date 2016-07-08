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
        // TODO: add check for existed user
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
