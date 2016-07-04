package main;

import accounts.AccountService;
import dbService.DBServiceImpl;
import dbService.DBServiceThreadImpl;
import game.GameServiceImpl;
import interfaces.DBService;
import interfaces.Frontend;
import interfaces.GameServiceThread;
import interfaces.GameTimerThread;
import messageSystem.AddressService;
import messageSystem.MessageSystem;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import services.GameTimerThreadImpl;
import servlets.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int port = Integer.parseInt(properties.getProperty("port"));

        // Services
        DBServiceImpl dbServiceImpl = new DBServiceImpl(properties);
        dbServiceImpl.printConnectInfo();

        AccountService accountService = new AccountService();

        Frontend frontend = new FrontendImpl();

        // Service container
        DIC dic = new DIC();
        dic.addServiceWithContext(DBService.class, dbServiceImpl);
        dic.addServiceWithContext(AccountService.class, accountService);
        dic.add(Frontend.class, frontend);

        // Create Address service
        AddressService addressService = new AddressService();

        // Create message system
        MessageSystem messageSystem = new MessageSystem(addressService);

        // For list of games I need GameServir in dic
        GameServiceImpl runnableGameService = new GameServiceImpl(messageSystem);

        dic.add(GameServiceThread.class, runnableGameService);
        dic.start();

//        accountService.addNewUser(new UsersDataSet("admin", "admin", "admin@localhost.ru"));
//        accountService.addNewUser(new UsersDataSet("test", "test", "test@localhost.ru"));
//        accountService.addNewUser(new UsersDataSet("Hleb", "1234", "hlebon@localhost.ru"));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new UsersServlet(accountService)), "/api/v1/users");
        context.addServlet(new ServletHolder(new SessionsServlet(accountService)), "/api/v1/sessions");

        context.addServlet(new ServletHolder(new SignUpServlet(dic)), "/signup");
        context.addServlet(new ServletHolder(new SignInServlet(dic)), "/signin");
        context.addServlet(new ServletHolder(new SignOutServlet(dic)), "/signout");
        context.addServlet(new ServletHolder(new MainServlet(dic)), "/homepage");
        context.addServlet(new ServletHolder(new ArenaServlet(dic)), "/arena");
        context.addServlet(new ServletHolder(new WebSocketChatServlet()), "/chat");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});

        Server server = new Server(port);
        server.setHandler(handlers);

        // Create Abonents
        properties.put("hibernate_hbm2ddl_auto", "validate"); // чтобы таблица не drop'алась
        DBServiceThreadImpl runnableDbService = new DBServiceThreadImpl(properties, messageSystem);
//        AccountServiceThreadImpl runnableAccountService = new AccountServiceThreadImpl(messageSystem, dic);
        GameTimerThread runnableGameTimerService = new GameTimerThreadImpl(messageSystem);
//        FrontendImpl runnableFrontendService = new FrontendImpl(messageSystem);

        // Fill message system
        messageSystem.addAbonent(runnableDbService);
        messageSystem.getAddressService().addDBService(runnableDbService);

//        messageSystem.addAbonent(runnableAccountService);
//        messageSystem.getAddressService().addAccountService(runnableAccountService);

        messageSystem.addAbonent(runnableGameService);
        messageSystem.getAddressService().addGameService(runnableGameService);

        messageSystem.addAbonent(runnableGameTimerService);
        messageSystem.getAddressService().addGameTimerService(runnableGameTimerService);

        context.addServlet(new ServletHolder(new WebSocketArenaServlet(runnableGameService)), "/game");

//        messageSystem.addAbonent(runnableFrontendService);

        // Start services
        (new Thread(runnableDbService)).start();
//        (new Thread(runnableAccountService)).start();
        (new Thread(runnableGameService)).start();
        (new Thread(runnableGameTimerService)).start();
//        (new Thread(runnableFrontendService)).start();

        server.start();
        server.join();
    }
}