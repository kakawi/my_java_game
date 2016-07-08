package servlets;

import accounts.AccountService;
import com.google.gson.Gson;
import dbService.dataSets.UsersDataSet;
import interfaces.Frontend;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignInServlet extends HttpServlet {
    private ApplicationContext applicationContext;

    public SignInServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("method", request.getMethod());
        pageVariables.put("URL", request.getRequestURL().toString());
//        pageVariables.put("pathInfo", request.getPathInfo());
        pageVariables.put("sessionId", request.getSession().getId());
        pageVariables.put("parameters", request.getParameterMap().toString());
        return pageVariables;
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
                return;
            }

//            accountService.addSession(req.getSession().getId(), user);
            req.getSession().setAttribute("profile", user);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            response.sendRedirect("/homepage?flash=" + "Login " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
