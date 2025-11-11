package com.core;

import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 *
 * @author martin on 11/4/25
 */
public class Mysql8Container extends AbstractCsContainer {
    private static final String rootPassword = "q";

    public Mysql8Container(String dbName, String user, String userPassword, Network network) {
        this(CsContainerConstants.Mysql8.VERSION, dbName, user, userPassword, "mysql8", network);
    }

    public Mysql8Container(String containerVersion, String dbName, String user, String userPassword, Network network) {
        this(containerVersion, dbName, user, userPassword, "mysql8", network);
    }

    public Mysql8Container(String containerVersion, String dbName, String user, String userPassword, String networkAlias, Network network) {
        super(containerVersion, networkAlias, network);

        // Build the base image name safely
        DockerImageName image = DockerImageName
                .parse(CsContainerConstants.Mysql8.IMAGE_NAME) // e.g. "mysql"
                .withTag(containerVersion);                    // e.g. "8.0.36"

        this.container = createBasicContainer(image.asCanonicalNameString(), containerVersion, network, false)
                // ✅ correct root password variable
                .withEnv("MYSQL_ROOT_PASSWORD", rootPassword)
                .withEnv("MYSQL_DATABASE", dbName)
                .withEnv("MYSQL_USER", user)
                .withEnv("MYSQL_PASSWORD", userPassword)
                // ✅ make it resolvable as "mysql8"
                .withNetworkAliases(networkAlias)
                // expose 3306 (internal DB port)
                .withExposedPorts(CsContainerConstants.Mysql8.DB_PORT)
                // ✅ wait until MySQL is ready before starting dependent containers
                .waitingFor(Wait.forLogMessage(".*ready for connections.*\\n", 1));
    }

    @Override
    public Integer getMappedPort() {
        return container.getMappedPort(CsContainerConstants.Mysql8.DB_PORT);
    }

    public String getDbName() {
        return getEnvValue("MYSQL_DATABASE");
    }

    public String getUser() {
        return getEnvValue("MYSQL_USER");
    }

    public String getUserPassword() {
        return getEnvValue("MYSQL_PASSWORD");
    }

    public String getMariaNetworkConnectionUrl() {
        return ContainerUtils.createMariaDb8JdbcUrl(getNetworkAlias(), CsContainerConstants.Mysql8.DB_PORT, getDbName());
    }

    public String getMysqlNetworkConnectionUrl() {
        return ContainerUtils.createMysql8JdbcUrl(getNetworkAlias(), CsContainerConstants.Mysql8.DB_PORT, getDbName());
    }

}
