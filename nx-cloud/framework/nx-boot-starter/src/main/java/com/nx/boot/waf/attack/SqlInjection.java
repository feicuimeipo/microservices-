package com.nx.boot.waf.attack;

/**
 * SQL注入攻击
 */
public class SqlInjection implements IStrip {

    /**
     * @Description SQL注入内容剥离
     */
    @Override
    public String strip(String value) {

        //剥离SQL注入部分代码
        return value.replaceAll("('.+--)|(\\|)|(%7C)", "");
    }
}
