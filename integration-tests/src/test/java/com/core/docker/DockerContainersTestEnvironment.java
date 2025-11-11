package com.core.docker;

import com.core.*;
import org.example.*;
import org.example.app.DaoProviderFactoryImpl;
import org.example.app.EndpointRegistryImpl;
import org.example.entities.Message;
import org.example.entities.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.testcontainers.containers.Network;

import java.util.HashMap;
import java.util.Map;

public class DockerContainersTestEnvironment extends AbstractDockerEnvironment implements MsgTestEnvironment {

    private static final String MSG_VERSION   = "junit.extension.msg.image.version";
    private static final String DATABASE_NAME = "messenger";
    private static final String DATABASE_USER = "db_user";
    private static final String DATABASE_PASS = "db_pass";
    private static final String ARTEMIS_USER = ""
            + "admin";
    private static final String ARTEMIS_PASSWORD = "admin";

    private Mysql8Container mysqlContainer;
    private MsgContainer    msgContainer;
    private MsgArtemisContainer   msgArtemisContainer;

    private final String          msgImageVersion;
    private       EndpointRegistry endpointRegistry;

    private static SessionFactory sessionFactory;

    public DockerContainersTestEnvironment(EnvironmentConfig configProvider) {
        super(configProvider);
        this.msgImageVersion = configProvider.getValue(MSG_VERSION, null);
    }

    @Override
    public void bootstrap() {
        final Network network = Network.builder().build();

        msgArtemisContainer = new MsgArtemisContainer(msgImageVersion, network, ARTEMIS_USER, ARTEMIS_PASSWORD);
        mysqlContainer      = new Mysql8Container(inhouseContainersVersion, DATABASE_NAME, DATABASE_USER, DATABASE_PASS, network);
        msgContainer        = new MsgContainer(msgImageVersion, network, mysqlContainer, msgArtemisContainer);

        // Start in dependency order
        ContainerUtils.run(msgArtemisContainer);
        ContainerUtils.run(mysqlContainer);
        ContainerUtils.run(msgContainer);

        logContainersInfo();
    }

    @Override
    public void resetDatabaseData() {
        getDaoProviderFactory(); // ensures sessionFactory is built
        HibernateUtils.eraseMySqlTables(sessionFactory);
    }

    private void logContainersInfo() {
        logger.info("====== Containers ======");
        String offset = "\t";
        msgContainer.logInfo("Messenger", offset);
        mysqlContainer.logInfo("MySQL", offset);
        msgArtemisContainer.logInfo("Artemis", offset);
    }

    @Override
    public void shutdown() {
        try {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        } catch (Exception ignored) {
        }
        // If you own container lifecycle here, you can stop them as well.
        // ContainerUtils.stopQuietly(msgContainer, mysqlContainer, msgArtemisContainer);
    }

    @Override
    public String getBaseRestUri() {
        String baseUri = String.format(
                "http://%s:%s/api",
                msgContainer.getContainerIpAddress(),
                msgContainer.getMappedPort()
        );
        logger.info("<<<<<<<<<<>>>>>>>>>>>>");
        logger.info(baseUri);
        logger.info("<<<<<<<<<<>>>>>>>>>>>>");
        return baseUri;
    }

    @Override
    public CsStoreAccessor getStoreAccessor() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public EndpointRegistry getEndpointRegistry() {
        if (endpointRegistry == null) {
            endpointRegistry = new EndpointRegistryImpl(
                    msgArtemisContainer.createConnectionFactory(),
                    "IntegrationTestsId"
            );
        }
        return endpointRegistry;
    }

    @Override
    public DaoProviderFactory getDaoProviderFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return new DaoProviderFactoryImpl(sessionFactory);
    }

    private SessionFactory buildSessionFactory() {
        // Since this JVM runs on the host, use host IP + mapped port.
        final String jdbcUrl = ContainerUtils.createJdbcUrl(
                mysqlContainer.getContainerIpAddress(),
                mysqlContainer.getMappedPort(),
                mysqlContainer.getDbName()
        );

        // Hibernate settings (no hibernate.cfg.xml)
        final Map<String, Object> settings = new HashMap<>();
        settings.put(AvailableSettings.DRIVER, "com.mysql.cj.jdbc.Driver");
        settings.put(AvailableSettings.URL, jdbcUrl);
        settings.put(AvailableSettings.USER, mysqlContainer.getUser());
        settings.put(AvailableSettings.PASS, mysqlContainer.getUserPassword());

        // Modern dialect (Hibernate 6+). If you’re on 5.6, MySQL5Dialect is fine.
        settings.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");

        // DDL strategy for tests: pick what you need (validate|update|create|create-drop)
        settings.put(AvailableSettings.HBM2DDL_AUTO, "update");
        settings.put(AvailableSettings.SHOW_SQL, "true");
        settings.put(AvailableSettings.FORMAT_SQL, "true");

        // Recommended MySQL options
        settings.put(AvailableSettings.JDBC_TIME_ZONE, "UTC");

        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        // Register annotated entities explicitly
        return new MetadataSources(serviceRegistry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Message.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
