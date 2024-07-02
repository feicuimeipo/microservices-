package com.nx.boot.waf.attack;

/**
 * 攻击过滤父类
 */
public interface IStrip {

    /**
     * @param value 待处理内容
     * @return
     * @Description 脚本内容剥离
     */
    public String strip(String value);
}
