package servlets;

import accounts.AccountService;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class TestServlet extends HttpServlet {
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, Object> pageVariables = new HashMap<>();
        String sessionId = req.getSession().getId();

        pageVariables.put("SessionID", sessionId);
            pageVariables.put("message", "Das Test");

        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        resp.getWriter().println(PageGenerator.instance().getPage("main/homepage.html", pageVariables));
    }
}
