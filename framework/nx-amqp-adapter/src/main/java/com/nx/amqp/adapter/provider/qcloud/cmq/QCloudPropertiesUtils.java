package com.nx.amqp.adapter.provider.qcloud.cmq;

import com.nx.amqp.adapter.MQProviderConfig;
import com.qcloud.cmq.QueueMeta;
import com.qcloud.cmq.entity.CmqConfig;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class QCloudPropertiesUtils {

    @SneakyThrows
    public static <T> CmqConfig buildCmqConfig(MQProviderConfig MQProviderConfig) {
        CmqConfig config = new CmqConfig();
        Class<CmqConfig> clazz = CmqConfig.class;
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
            propValue = MQProviderConfig.getKafka().getPropValue(propName);
            if (StringUtils.isNotBlank(propValue)) {
                field.setAccessible(true);
                field.set(config,propValue);
            }
        }
        return config;
    }

    @SneakyThrows
    public static <T> QueueMeta buildQueueMeta(MQProviderConfig MQProviderConfig) {
        QueueMeta config = new QueueMeta();
        Class<QueueMeta> clazz = QueueMeta.class;
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
            propValue = MQProviderConfig.getKafka().getPropValue(propName);
            if (StringUtils.isNotBlank(propValue)) {
                field.setAccessible(true);
                field.set(config,propValue);
            }
        }
        return config;
    }

}
