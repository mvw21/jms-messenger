package org.example.app.spring;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.example.DeserializationProcessor;
import org.example.Endpoint;
import org.example.app.response.ResponseSender;
import org.example.constants.MsgConstants;
import org.example.dtos.AbstractDto;
import org.example.dtos.ErrorDto;
import org.example.exception.MessengerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class RequestListener implements MessageListener
{
    private static final Logger         logger = LoggerFactory.getLogger(RequestListener.class);
    private final ResponseSender responseSender;
    private final Endpoint       endpointWorker;

    public RequestListener(ResponseSender responseSender, Endpoint endpointWorker)
    {
        this.responseSender = responseSender;
        this.endpointWorker = endpointWorker;
    }

    @Override
    public void onMessage(Message message)
    {
        try
        {
            message.acknowledge();
            logger.info("Received Message {}", message.getJMSCorrelationID());

            if (message instanceof TextMessage textMessage)
            {
                String operationName = message.getStringProperty(MsgConstants.OPERATION_NAME);

                AbstractDto abstractDto = DeserializationProcessor.instance.deserialize(textMessage.getText());

                sendResponseToWorker(operationName, abstractDto, message);
            }
            else
            {
                throw new JMSException("Message is not the correct TextMessage format.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //sendResponseToWorker или navigateResponseToWorker
    private void sendResponseToWorker(String operationName, AbstractDto abstractDto,Message message) throws InvocationTargetException, IllegalAccessException
    {
        Method correctMethod = findCorrectMethod(operationName);
        CompletableFuture<?> completableFuture = (CompletableFuture<?>)correctMethod.invoke(endpointWorker, abstractDto);

        getResultFromWorkerAndCompleteTheFuture(completableFuture,message);
    }

    private void getResultFromWorkerAndCompleteTheFuture(CompletableFuture<?> completableFuture, Message message)
    {
        completableFuture.whenComplete((result, throwable) -> {
            try {
                if (throwable == null) {
                    responseSender.sendResponse((AbstractDto) result, message.getJMSReplyTo(), message.getJMSCorrelationID());
                } else {
                    Throwable cause = (throwable instanceof java.util.concurrent.CompletionException ce && ce.getCause() != null)
                            ? ce.getCause()
                            : throwable;

                    ErrorDto errorDto;
                    if (cause instanceof MessengerException me) {
                        errorDto = me.getErrorDto();
                    } else {
                        // log the unexpected cause so you see the real stack trace
                        logger.error("Unhandled exception while processing op={}, cid={}",
                                message.getStringProperty(MsgConstants.OPERATION_NAME),
                                message.getJMSCorrelationID(), cause);
                        errorDto = new ErrorDto(
                                org.example.dtos.ErrorCodesDto.INTERNAL_SERVER_ERROR,
                                java.util.List.of("Unexpected error. Please try again.")
                        );
                    }
                    responseSender.sendResponse(errorDto, message.getJMSReplyTo(), message.getJMSCorrelationID());
                }
            } catch (JMSException e) {
                logger.error("Failed to send response", e);
            }
        });


    }

    private Method findCorrectMethod(String operationName)
    {

        for (Method m : endpointWorker.getClass().getMethods())
        {
            if(m.getName().equals(operationName)){
                return m;
            }
        }
        throw new IllegalArgumentException("No such operation " + operationName);
    }

}
