package org.example.app.jms.receiver;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.example.DeserializationProcessor;
import org.example.dtos.AbstractDto;
import org.example.dtos.ErrorDto;
import org.example.exception.MessengerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseListener implements MessageListener
{
    private static final Logger                         logger     = LoggerFactory.getLogger(ResponseListener.class);
    private final          Map<String, CompletableFuture<?>> futureById = new ConcurrentHashMap<>();

    public void attachCompletableFuture(String correlationId,CompletableFuture<?> future)
    {
        futureById.put(correlationId,future);
    }

//    @Override
//    public void onMessage(Message message)
//    {
//        try
//        {
//                logger.info("Recieved: {}" ,message.getJMSCorrelationID());
//
//            if (message instanceof TextMessage textMessage){
//                //1.get completableFuture and 2.remove it from the map before 3.complete it
//                CompletableFuture completableFuture = futureById.remove(message.getJMSCorrelationID());
//
//                if (completableFuture == null)
//                {
//                    throw new IllegalArgumentException(message.getJMSCorrelationID());
//                }
//
//                AbstractDto responseDto = DeserializationProcessor.instance.deserialize(textMessage.getText());
//
//                //complete the future
//                if(responseDto instanceof ErrorDto errorDto){
//                    logger.info("{{{{{{ 8 }}}}}}}");
//                   completableFuture.completeExceptionally(new MessengerException(errorDto));
//                    logger.info("{{{{{{ 11 }}}}}}}");
//                }else {
//                    logger.info("{{{{{{ 9 }}}}}}}");
//                    completableFuture.complete(responseDto);
//                    logger.info("{{{{{{ 10 }}}}}}}");
//                }
//
//            }else{
//                throw new JMSException("Message is not the correct TextMessage format.");
//            }
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onMessage(Message message) {
        try {
            String cid = message.getJMSCorrelationID();
            logger.info("Received: {}", cid);

            if (!(message instanceof TextMessage tm)) {
                throw new JMSException("Message is not the correct TextMessage format.");
            }

            CompletableFuture<?> cf = futureById.remove(cid);
            if (cf == null) {
                logger.warn("No waiting future for CID={}, dropping. Text={}", cid, tm.getText());
                return; // don't blow up the listener
            }

            AbstractDto responseDto = DeserializationProcessor.instance.deserialize(tm.getText());
            if (responseDto instanceof ErrorDto errorDto) {
                cf.completeExceptionally(new MessengerException(errorDto));
            } else {
                @SuppressWarnings("unchecked")
                CompletableFuture<Object> typed = (CompletableFuture<Object>) cf;
                typed.complete(responseDto);
            }
        } catch (Exception e) {
            logger.error("Failed to process response", e);
        }
    }
}
