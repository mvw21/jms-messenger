package com.core.docker;

import com.core.AbstractCsContainer;
import com.core.ContainerUtils;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class MsgArtemisContainer extends AbstractCsContainer
{

    // 61616 = OpenWire, 8161 = web console (optional)
    private static final int JMS_PORT = MsgContainerConstants.Artemis.JMS_TCP_PORT;

    public MsgArtemisContainer(String containerVersion, Network network, String user, String password) {
        super(containerVersion, "msg-broker", network);

        this.container = new GenericContainer<>(
                MsgContainerConstants.Artemis.IMAGE_NAME + ":" + containerVersion
        )
                // ensure a *fresh* instance each run; don't mount /var/lib/artemis-instance in tests
                .withEnv("ARTEMIS_USER", user)
                .withEnv("ARTEMIS_PASSWORD", password)
                .withEnv("ANONYMOUS_LOGIN", "false")
                // optional JVM tuning
                .withEnv("JAVA_OPTS", "-Xms256m -Xmx512m")
                .withNetworkAliases(getNetworkAlias())
                .withExposedPorts(JMS_PORT /*, 8161 */)
                .waitingFor(Wait.forListeningPort())
                .withLogConsumer(createLogConsumer("containers.artemis"))
                .withNetwork(network);

        // If you ever see a tag that doesn’t auto-create the instance from env vars,
        // uncomment this to force an explicit create+run every time:
        /*
        this.container
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("bash", "-lc"))
            .withCommand(
                "/opt/activemq-artemis/bin/artemis create --silent " +
                " --user $ARTEMIS_USER --password $ARTEMIS_PASSWORD " +
                " --role admin --allow-anonymous no /var/lib/artemis-instance && " +
                "/var/lib/artemis-instance/bin/artemis run"
            );
        */

        }

    public String getUser() {
        return getEnvValue("ARTEMIS_USER");
    }

    public String getPassword() {
        return getEnvValue("ARTEMIS_PASSWORD");
    }

    @Override
    public Integer getMappedPort()
    {
        return container.getMappedPort(MsgContainerConstants.Artemis.JMS_TCP_PORT);
    }

    public ActiveMQConnectionFactory createConnectionFactory() {
        return new ActiveMQConnectionFactory(ContainerUtils.createBrokerUrl(getContainerIpAddress(), getMappedPort()), getUser(), getPassword());
    }
}
