package main;

import interfaces.Frontend;
import templater.PageGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class FrontendImpl implements Frontend {
    @Override
    public void showPage(HttpServletResponse response, String filename) throws IOException{
        response.getWriter().println(PageGenerator.instance().getPage(filename));

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void showPage(HttpServletResponse response, String filename, Map<String, Object> data) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().println(PageGenerator.instance().getPage(filename, data));
    }
}
