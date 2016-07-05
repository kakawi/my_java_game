package dbService;

import dbService.dao.UsersDAO;
import dbService.dataSets.UsersDataSet;
import interfaces.DBService;
import interfaces.serviceWithDIC;
import main.DIC;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBServiceImpl implements DBService, serviceWithDIC {
    private Properties properties = new Properties();
    { // default properties
        properties.put("db", "h2");
        properties.put("hibernate_show_sql", "true");
        properties.put("hibernate_hbm2ddl_auto", "create");
    }
    private SessionFactory sessionFactory;
    private DIC DIC;

    public void setDIC(DIC DIC) {
        this.DIC = DIC;
    }

    public DBServiceImpl(Properties properties) {
        this.properties.putAll(properties);

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


    public UsersDataSet getUser(long id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            UsersDataSet dataSet = dao.get(id);
            session.close();
            return dataSet;
        } catch (HibernateException e) {
            throw new DBException(e);
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

    public long addUser(String name) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            UsersDAO dao = new UsersDAO(session);
            long id = dao.insertUser(name);
            transaction.commit();
            session.close();
            return id;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public long addUser(UsersDataSet user) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            UsersDAO dao = new UsersDAO(session);
            long id = dao.insertUser(user);
            transaction.commit();
            session.close();
            return id;
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void printConnectInfo() {
        try {
            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
            Connection connection = sessionFactoryImpl.getConnectionProvider().getConnection();
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
