package com.yuhao.weblog.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 配置：支持 Java 8 日期，统一 yyyy-MM-dd / yyyy-MM-dd HH:mm:ss 格式
 * 并将 Long 序列化为字符串，避免雪花 ID（19 位）超出 JS 精度被截断
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FMT));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FMT));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FMT));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FMT));

        // Long -> String，防止雪花 ID 精度丢失；反序列化时字符串/数字均转回 Long
        module.addSerializer(Long.class, ToStringSerializer.INSTANCE);
        module.addSerializer(Long.TYPE, ToStringSerializer.INSTANCE);
        module.addDeserializer(Long.class, LongDeserializer.INSTANCE);
        module.addDeserializer(Long.TYPE, LongDeserializer.INSTANCE);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略请求 JSON 中的未知字段，避免前后端字段不一致导致反序列化失败
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    /** Long 序列化为字符串 */
    static class ToStringSerializer extends JsonSerializer<Long> {
        static final ToStringSerializer INSTANCE = new ToStringSerializer();

        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }

    /** 字符串/数字反序列化为 Long */
    static class LongDeserializer extends JsonDeserializer<Long> {
        static final LongDeserializer INSTANCE = new LongDeserializer();

        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken().isNumeric()) {
                return p.getLongValue();
            }
            String s = p.getText();
            if (s == null || s.isEmpty()) {
                return null;
            }
            return Long.valueOf(s.trim());
        }
    }
}
