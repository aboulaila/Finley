package com.mixedmug.finley.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.Message;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@WritingConverter
@Component
public class MessagesWritingConverter implements Converter<List<Message>, Json> {

    private final ObjectMapper objectMapper;

    public MessagesWritingConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Json convert(List<Message> source) {
        try {
            return Json.of(objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to write messages as JSON", e);
        }
    }
}