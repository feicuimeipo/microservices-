
package com.nx.datasource.core.shardingsphere;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * 自定义in和=的精准匹配算法
 * 自定义between and、>=、<=、>、<等范围匹配算法
 */
@Slf4j
public class DatePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<String> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> preciseShardingValue) {
        String dateTime = preciseShardingValue.getValue();
        log.info("Sharding input:" + preciseShardingValue.getValue());
        String suffix = getSuffixByYearMonthDay(dateTime);
        for (String tableName : availableTargetNames) {
            log.info("suffix:" + suffix + ", 表明:{}" + tableName);
            if (tableName.endsWith(suffix)) {
                return tableName;
            }
        }
        throw new IllegalArgumentException("未找到匹配的数据表");
    }

    private static String getSuffixByYearMonthDay(String dateTime) {
        return StringUtils.substring(dateTime, 0, 4);
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<String> rangeShardingValue) {
        String startTime = rangeShardingValue.getValueRange().lowerEndpoint();
        String endTime = rangeShardingValue.getValueRange().upperEndpoint();
        String startTimeSuffix = getSuffixByYearMonthDay(startTime);
        String endTimeSuffix = getSuffixByYearMonthDay(endTime);
        Collection<String> collect = Sets.newHashSet();
        for (String tableName : collection) {
            if (tableName.endsWith(startTimeSuffix)) {
                collect.add(tableName);
                continue;
            }
            if (tableName.endsWith(endTimeSuffix)) {
                collect.add(tableName);
                continue;
            }
        }
        if (CollectionUtils.isEmpty(collect)) {
            throw new IllegalArgumentException("未找到匹配的数据表");
        }
        return collect;
    }
}
