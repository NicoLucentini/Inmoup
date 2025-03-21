package org.example.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.InmoupProperty;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class InmoupHelper {

    public static ByteArrayResource convertToJson(List<InmoupProperty> properties) throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(properties);

        // Convert JSON string to bytes
        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(jsonBytes);
    }
}
