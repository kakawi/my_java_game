package dbService;

import dbService.dao.UsersDAO;
import dbService.dataSets.UsersDataSet;
import interfaces.DBServiceThread;
import messageSystem.AddressImpl;
import messageSystem.MessageSystem;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class DBServiceThreadImpl implements DBServiceThread {

    private AddressImpl address = new AddressImpl();

    private Properties properties = new Properties();
    { // default properties
        properties.put("db", "h2");
        properties.put("hibernate_show_sql", "true");
        properties.put("hibernate_hbm2ddl_auto", "create");
    }
    private SessionFactory sessionFactory;

    private MessageSystem messageSystem;

    @Override
    public MessageSystem getMessageSystem() {
        return messageSystem;
    }

    public DBServiceThreadImpl(Properties properties, MessageSystem messageSystem) {
        this.properties.putAll(properties);

        this.messageSystem = messageSystem;

        if ("mysql".equals(this.properties.getProperty("db"))) {
            Configuration configuration = getMySqlConfiguration();
            sessionFactory = createSessionFactory(configuration);
        } else if ("h2".equals(this.properties.getProperty("db"))) {
            Configuration configuration = getH2Configuration();
            sessionFactory = createSessionFactory(configuration);
        }
    }

    private Configuration getMySqlConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UsersDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://" + this.properties.getProperty("db_host") + ":3306/" + this.properties.getProperty("db_name"));
        configuration.setProperty("hibernate.connection.username",  this.properties.getProperty("db_username"));
        configuration.setProperty("hibernate.connection.password", this.properties.getProperty("db_password"));
        configuration.setProperty("hibernate.show_sql", this.properties.getProperty("hibernate_show_sql"));
        configuration.setProperty("hibernate.hbm2ddl.auto", this.properties.getProperty("hibernate_hbm2ddl_auto"));
        return configuration;
    }

    private Configuration getH2Configuration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UsersDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./h2db");
        configuration.setProperty("hibernate.connection.username", "tully");
        configuration.setProperty("hibernate.connection.password", "tully");
        configuration.setProperty("hibernate.show_sql", this.properties.getProperty("hibernate_show_sql"));
        configuration.setProperty("hibernate.hbm2ddl.auto", this.properties.getProperty("hibernate_hbm2ddl_auto"));
        return configuration;
    }

    private SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Override
    public AddressImpl getAddress() {
        return address;
    }

    @Override
    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UsersDataSet getUserByLogin(String login) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            UsersDataSet dataSet = dao.getUserByLogin(login);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void update(UsersDataSet usersDataSet) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.update(usersDataSet);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }
}
