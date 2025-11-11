package org.example.app;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.example.dtos.message.MessageRequestDto;

import java.util.UUID;

/**
 *
 * @author martin on 11/4/25
 */
public class ClientMain {
    public static void main(String[] args) throws Exception {
        String brokerUrl = getenv("BROKER_URL", "tcp://msg-broker:61616");
        String user = getenv("BROKER_USERNAME", "admin");
        String pass = getenv("BROKER_PASSWORD", "admin");
        String queueName = getenv("QUEUE", "queue.test");
        String payload = getenv("PAYLOAD", "Hello from jms-client");

        System.out.printf("Connecting to %s as %s, queue=%s%n", brokerUrl, user, queueName);

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl, user, pass);
             JMSContext ctx = cf.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {

            Queue q = ctx.createQueue(queueName);
            ctx.createProducer().send(q, payload);
            System.out.println("Sent message: " + payload);
        }
    }

    private static String getenv(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? def : v;
    }
}
