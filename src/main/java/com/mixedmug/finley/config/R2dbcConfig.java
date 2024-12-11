package com.mixedmug.finley.config;

import com.mixedmug.finley.converter.MessagesReadingConverter;
import com.mixedmug.finley.converter.MessagesWritingConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class R2dbcConfig {
    private final MessagesWritingConverter messagesWritingConverter;
    private final MessagesReadingConverter messagesReadingConverter;

    public R2dbcConfig(MessagesWritingConverter messagesWritingConverter, MessagesReadingConverter messagesReadingConverter) {
        this.messagesWritingConverter = messagesWritingConverter;
        this.messagesReadingConverter = messagesReadingConverter;
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(messagesWritingConverter);
        converters.add(messagesReadingConverter);
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, converters);
    }
}