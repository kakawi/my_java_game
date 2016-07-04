package servlets;

import accounts.AccountService;
import dbService.dataSets.UsersDataSet;
import main.DIC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignOutServlet extends HttpServlet {
    private DIC dic;

    public SignOutServlet(DIC dic) {
        this.dic = dic;
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
        String sessionId = request.getSession().getId();
        AccountService accountService = dic.get(AccountService.class);
        UsersDataSet user = accountService.getUserBySessionId(sessionId);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/homepage?flash=" + "You're unauthorized");
        } else {
            accountService.deleteSession(sessionId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.sendRedirect("/homepage?flash=Bye-bye");
        }
    }
}
