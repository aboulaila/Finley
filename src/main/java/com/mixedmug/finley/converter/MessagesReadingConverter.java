package com.mixedmug.finley.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.Message;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@ReadingConverter
@Component
public class MessagesReadingConverter implements Converter<Json, List<Message>> {

    private final ObjectMapper objectMapper;

    public MessagesReadingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Message> convert(Json json) {
        try {
            return objectMapper.readValue(json.asString(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read messages from JSON", e);
        }
    }
}