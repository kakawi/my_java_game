package interfaces;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface Frontend {
    void showPage(HttpServletResponse response, String filename) throws IOException;

    void showPage(HttpServletResponse response, String filename, Map<String, Object> data) throws IOException;
}
