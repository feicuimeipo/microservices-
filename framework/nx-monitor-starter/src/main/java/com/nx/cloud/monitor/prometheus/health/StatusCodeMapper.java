package com.nx.cloud.monitor.prometheus.health;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;


@Component
@Log4j2
public class StatusCodeMapper implements HttpCodeStatusMapper {
    /**
     *
     * UNKNOWN:未知状态，映射HTTP状态码为503
     * UP：正常，映射HTTP状态码为200
     * DOWN：失败，映射HTTP状态码为503
     * OUT_OF_SERVICE:不能对外提供服务，但是服务正常。映射HTTP状态码为200
     *
     * */
    @Override
    public int getStatusCode(Status status) {
        log.info("云平台状态代码转义");
        if (status == Status.DOWN) {
            return 500;
        }

        if (status == Status.OUT_OF_SERVICE) {
            return 503;
        }

        if (status == Status.UNKNOWN) {
            return 500;
        }

        if (status.getCode().equals("WARNING")) {
            return 500;
        }

        return 200;
    }
}
