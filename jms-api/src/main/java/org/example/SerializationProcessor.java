package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.AbstractDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SerializationProcessor
{
    public static final SerializationProcessor instance = new SerializationProcessor();

    private static final ObjectMapper objectMapper = ObjectMapperSingleton.instance.getObjectMapper();

    private SerializationProcessor()
    {
        // Singleton
    }

    public String serialize(AbstractDto entity) throws IOException
    {
        return jsonSerialize(entity);
    }

    private String jsonSerialize(Object payload) throws IOException
    {
        if (payload == null)
        {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            objectMapper.writeValue(out, payload);
            return out.toString(StandardCharsets.UTF_8);
        }
    }

}
