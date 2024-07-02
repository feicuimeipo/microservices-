package com.nx.logger.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionLogType {
    httpRequest(1),schedule(2),messageQueue(3);    ;
    /**
     * 类型
     */
    private final Integer type;
}
