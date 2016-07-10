package main;

import interfaces.DBServiceThread;
import interfaces.GameServiceThread;
import interfaces.GameTimerThread;
import messageSystem.MessageSystem;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import servlets.*;

public class Main {
    public static void main(String[] args) throws Exception {
        AbstractApplicationContext springContext = new ClassPathXmlApplicationContext("beans.xml");

//        accountService.addNewUser(new UsersDataSet("admin", "admin", "admin@localhost.ru"));
//        accountService.addNewUser(new UsersDataSet("test", "test", "test@localhost.ru"));
//        accountService.addNewUser(new UsersDataSet("Hleb", "1234", "hlebon@localhost.ru"));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");

        context.addServlet(new ServletHolder(new SignUpServlet(springContext)), "/signup");
        context.addServlet(new ServletHolder(new SignInServlet(springContext)), "/signin");
        context.addServlet(new ServletHolder(new SignOutServlet(springContext)), "/signout");
        context.addServlet(new ServletHolder(new MainServlet(springContext)), "/homepage");
        context.addServlet(new ServletHolder(new ArenaServlet(springContext)), "/arena");
        context.addServlet(new ServletHolder(new WebSocketArenaServlet(springContext)), "/game");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(context);

        Server server = (Server) springContext.getBean("server");
        server.setHandler(handlers);

        // Create Abonents
        DBServiceThread dbServiceThread = (DBServiceThread) springContext.getBean("dbServiceThread");
        GameServiceThread gameServiceThread = (GameServiceThread) springContext.getBean("gameService");
        GameTimerThread gameTimerThread = (GameTimerThread) springContext.getBean("gameTimer");

        // Create message system
        MessageSystem messageSystem = (MessageSystem) springContext.getBean("messageSystem");

        // Fill message system
        messageSystem.addAbonent(dbServiceThread);
        messageSystem.getAddressService().addService(DBServiceThread.class, dbServiceThread);

        messageSystem.addAbonent(gameServiceThread);
        messageSystem.getAddressService().addService(GameServiceThread.class, gameServiceThread);

        messageSystem.addAbonent(gameTimerThread);
        messageSystem.getAddressService().addService(GameTimerThread.class, gameTimerThread);

        // Start services
        (new Thread(dbServiceThread)).start();
        (new Thread(gameServiceThread)).start();
        (new Thread(gameTimerThread)).start();



        server.start();
        server.join();
    }
}