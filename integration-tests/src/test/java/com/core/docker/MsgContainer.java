package com.core.docker;

import com.core.AbstractCsContainer;
import com.core.ContainerUtils;
import com.core.CsContainerConstants;
import com.core.Mysql8Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

public class MsgContainer extends AbstractCsContainer
{
    public MsgContainer(String containerVersion, Network network, Mysql8Container msgMysqlContainer,MsgArtemisContainer msgArtemisContainer)
    {
        super(containerVersion, "messenger", network);
        container = new GenericContainer<>(MsgContainerConstants.Messenger.IMAGE_NAME + ":" + containerVersion)
                .withEnv("PROFILE", "dev")
                // DB
                .withEnv("DB_URL", ContainerUtils.createJdbcUrl(
                        msgMysqlContainer.getNetworkAlias(), CsContainerConstants.Mysql8.DB_PORT, msgMysqlContainer.getDbName()))
                .withEnv("DB_USER", msgMysqlContainer.getUser())
                .withEnv("DB_PASSWORD", msgMysqlContainer.getUserPassword())
                // Broker
                .withEnv("BROKER_URL", ContainerUtils.createBrokerUrl(msgArtemisContainer.getNetworkAlias(),
                        MsgContainerConstants.Artemis.JMS_TCP_PORT))
                .withEnv("BROKER_USERNAME", msgArtemisContainer.getUser())
                .withEnv("BROKER_PASSWORD", msgArtemisContainer.getPassword())
                // ---- IMPORTANT: app must NOT generate/alter schema ----
                .withEnv("SPRING_JPA_HIBERNATE_DDL_AUTO", "none")                      // or "validate"
                .withEnv("SPRING_JPA_GENERATE_DDL", "false")
                .withEnv("SPRING_SQL_INIT_MODE", "never")                              // spring.sql.init.mode=never
                .withEnv("SPRING_FLYWAY_ENABLED", "false")
                // Keep UUIDs consistent even if app accidentally touches schema
                .withEnv("SPRING_JPA_PROPERTIES_HIBERNATE_TYPE_PREFERRED_UUID_JDBC_TYPE", "BINARY")
                // Mirror via system properties (highest precedence) to be extra-safe
                .withEnv("JAVA_OPTS",
                        "-Dspring.jpa.hibernate.ddl-auto=none " +
                                "-Dspring.jpa.generate-ddl=false " +
                                "-Dspring.sql.init.mode=never " +
                                "-Dspring.flyway.enabled=false " +
                                "-Dhibernate.type.preferred_uuid_jdbc_type=BINARY")
                .withExposedPorts(MsgContainerConstants.Messenger.HTTP_PORT)
                .withLogConsumer(createLogConsumer("containers.msg"))
                .withNetwork(network);
        //        container.setPortBindings(Arrays.asList("8787:8787"));
    }

    @Override
    public Integer getMappedPort()
    {
        return container.getMappedPort(MsgContainerConstants.Messenger.HTTP_PORT);
    }


}
