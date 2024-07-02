package com.nx.amqp.adapter.provider.kafka;

import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.utils.id.IdGeneratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.lang.reflect.Field;
import java.util.Properties;


public class KafkaPropertiesUtils {

    public static <T> Properties buildConsumerConfigs(MQProviderConfig config) {
        Properties result = new Properties();
        result.setProperty("group.id",config.getMqGroup());
        Class<ConsumerConfig> clazz = ConsumerConfig.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String propName;
            String propValue;

            if (!field.getName().endsWith("CONFIG") || field.getType() != String.class) continue;
            field.setAccessible(true);
            try {
                propName = field.get(clazz).toString();
            } catch (Exception e) {
                continue;
            }
            propValue = config.getKafka().getPropValue(propName);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(propValue)) {
                result.setProperty(propName, propValue);
            }
        }

        if (!result.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)) {
            throw new NullPointerException("Kafka config[bootstrap.servers] is required");
        }

        if (!result.containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)) {
            result.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // key serializer
        }

        if (!result.containsKey(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)) {
            result.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        }

        if (!result.containsKey(ConsumerConfig.CLIENT_ID_CONFIG)) {
            result.put(ConsumerConfig.CLIENT_ID_CONFIG, config.getMqGroup() + IdGeneratorUtils.nextId());
        }

        //每批次最大拉取记录
        if (!result.containsKey(ConsumerConfig.MAX_POLL_RECORDS_CONFIG)) {
            result.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getConsumer().getProcessThreads());
        }

        //设置默认提交
        if (!result.containsKey(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG)) {
            result.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, config.getConsumer().isAsync());
        }

        return result;
    }

    public static Properties buildProductConfigs(MQProviderConfig config) {
        Properties result = new Properties();

        Class<ProducerConfig> clazz = ProducerConfig.class;
        Field[] fields = clazz.getDeclaredFields();
        String propName;
        String propValue;
        for (Field field : fields) {
            if(!field.getName().endsWith("CONFIG") || field.getType() != String.class)continue;
            field.setAccessible(true);
            try {
                propName = field.get(clazz).toString();
            } catch (Exception e) {
                continue;
            }
            propValue = config.getKafka().getPropValue(propName);
            if(StringUtils.isNotBlank(propValue)) {
                result.setProperty(propName, propValue);
            }
        }

        if (!result.containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)) {
            throw new NullPointerException("Kafka config[bootstrap.servers] is required");
        }

        if(!result.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)){
            result.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key serializer
        }

        if(!result.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)){
            result.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        }

        //默认重试一次
        if(!result.containsKey(ProducerConfig.RETRIES_CONFIG)){
            result.put(ProducerConfig.RETRIES_CONFIG, "1");
        }

        if(!result.containsKey(ProducerConfig.COMPRESSION_TYPE_CONFIG)){
            result.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        }

        return result;
    }

}
