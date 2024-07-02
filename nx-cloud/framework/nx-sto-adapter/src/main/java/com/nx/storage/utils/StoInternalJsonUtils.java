package com.nx.storage.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class StoInternalJsonUtils {

   private static ObjectMapper jsonMapper = null;
   private static String JSON_OBJECT_PREFIX = "{";
   private static String JSON_ARRAY_PREFIX = "[";


   public static void setObjectMapper(ObjectMapper jsonMapper) {
       StoInternalJsonUtils.jsonMapper = jsonMapper;
   }

   public static ObjectMapper getMapper() {
       if (jsonMapper != null)
           return jsonMapper;
       synchronized (StoInternalJsonUtils.class) {
           if (jsonMapper != null)
               return jsonMapper;
           if (jsonMapper == null) {
               jsonMapper = new ObjectMapper();
               // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
               jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
               jsonMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
               jsonMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
               jsonMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
               jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
               jsonMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
               jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
               jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
           }
       }
       return jsonMapper;
   }




   public static String toJson(Object object) {
       try {
           return getMapper().writeValueAsString(object);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }



   public static <T> T toObject(String jsonString, Class<T> clazz) {
       if (StringUtils.isEmpty(jsonString)) {
           return null;
       }
       try {
           return getMapper().readValue(jsonString, clazz);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

   public static <K, V> Map<K, V> toHashMap(String jsonString, Class<K> keyType, Class<V> valueType) {
       if (StringUtils.isEmpty(jsonString)) {
           return null;
       }
       JavaType javaType = getMapper().getTypeFactory().constructParametricType(HashMap.class, keyType, valueType);
       return toObject(jsonString, javaType);
   }


   @SuppressWarnings("unchecked")
   public static <T> T toObject(String jsonString, JavaType javaType) {
       if (StringUtils.isEmpty(jsonString)) {
           return null;
       }
       try {
           return (T)getMapper().readValue(jsonString, javaType);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

   public static JsonNode getNode(String jsonString, String nodeName) {
       try {
           JsonNode node = getMapper().readTree(jsonString);
           return nodeName == null ? node : node.get(nodeName);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

   /**
    *
    * @param jsonString
    * @param attrs (e.g:info.user.id)
    * @return
    */
   public static String getJsonNodeValue(String jsonString, String attrs) {
       if (StringUtils.isBlank(jsonString))
           return null;
       return getJsonNodeValue(getNode(jsonString, null), attrs);
   }

   /**
    *
    * @param node
    * @param attrs (e.g:info.user.id)
    * @return
    */
   public static String getJsonNodeValue(JsonNode node, String attrs) {
       // ObjectNode,ArrayNode
       int index = attrs.indexOf(".");
       JsonNode subNode = null;
       if (index == -1) {
           if (node != null) {
               if (node instanceof ArrayNode) {
                   ArrayNode arrayNode = (ArrayNode)node;
                   subNode = arrayNode.isEmpty() ? null : arrayNode.get(0).get(attrs);
               } else {
                   subNode = node.get(attrs);
               }

               if (subNode == null)
                   return null;
               if (subNode instanceof ValueNode) {
                   return subNode.asText();
               }

               return subNode.toString();
           }
           return null;
       } else {
           String s1 = attrs.substring(0, index);
           String s2 = attrs.substring(index + 1);
           if (node instanceof ArrayNode) {
               ArrayNode arrayNode = (ArrayNode)node;
               subNode = arrayNode.isEmpty() ? null : arrayNode.get(0).get(s1);
           } else {
               subNode = node.get(s1);
           }
           return getJsonNodeValue(subNode, s2);
       }
   }


}