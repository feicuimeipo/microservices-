package com.nx.auth.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InternalJsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //设置JSON时间格式
        SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormat));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormat));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormat));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormat));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormat));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormat));
        mapper.registerModule(javaTimeModule);
        mapper.setDateFormat(myDateFormat);
    }

    /**
     * 对象转换为JsonNode
     * @param obj	对象
     * @return		JsonNode
     * @throws IOException
     */
    public static JsonNode toJsonNode(Object obj) {
        if(InternalBeanUtils.isEmpty(obj)) return null;
        return mapper.convertValue(obj, JsonNode.class);
    }

    public static <C> C toBean(JsonNode jsonNode, Class<C> cls) {
        Assert.notNull(jsonNode, "jsonNode can not be empty.");
        return mapper.convertValue(jsonNode, cls);
    }

    public static <C> C toBean(String json, Class<C> cls) throws IOException{
        return mapper.readValue(json, cls);
    }

    public static <C> C toBean(String json, TypeReference<C> typeRef) throws   IOException{
        C list = mapper.readValue(json, typeRef);
        return list;
    }


    public static String toJson(Object obj) throws IOException{
        return mapper.writeValueAsString(obj);
    }



    /**
     * 获取ObjectMapper
     * @return ObjectMapper
     */
    public static ObjectMapper getMapper(){
        return mapper;
    }

}
