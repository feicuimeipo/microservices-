package com.nx.elasticsearch.utils;

import java.io.Serializable;

/**
 * 作者：王坤造
 * 时间：2018/12/13
 * 名称：
 * 备注：
 */
public class EsDateInterval implements Serializable {
    public static final EsDateInterval SECOND = new EsDateInterval("1s");
    public static final EsDateInterval MINUTE = new EsDateInterval("1m");
    public static final EsDateInterval HOUR = new EsDateInterval("1h");
    public static final EsDateInterval DAY = new EsDateInterval("1d");
    public static final EsDateInterval WEEK = new EsDateInterval("1w");
    public static final EsDateInterval MONTH = new EsDateInterval("1M");
    public static final EsDateInterval QUARTER = new EsDateInterval("1q");//季度
    public static final EsDateInterval YEAR = new EsDateInterval("1y");
    private final String expression;

    private EsDateInterval(String expression) {
        this.expression = expression;
    }

    public static EsDateInterval seconds(int sec) {
        return new EsDateInterval(sec + "s");
    }

    public static EsDateInterval minutes(int min) {
        return new EsDateInterval(min + "m");
    }

    public static EsDateInterval hours(int hours) {
        return new EsDateInterval(hours + "h");
    }

    public static EsDateInterval days(int days) {
        return new EsDateInterval(days + "d");
    }

    public static EsDateInterval weeks(int weeks) {
        return new EsDateInterval(weeks + "w");
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        int length = expression.length();
        char c = expression.charAt(length - 1);
        if (length < 3 && expression.contains("1")) {
            switch (c) {
                case 's':
                    return "EsDateInterval.SECOND";
                case 'm':
                    return "EsDateInterval.MINUTE";
                case 'h':
                    return "EsDateInterval.HOUR";
                case 'd':
                    return "EsDateInterval.DAY";
                case 'w':
                    return "EsDateInterval.WEEK";
                case 'M':
                    return "EsDateInterval.MONTH";
                case 'q':
                    return "EsDateInterval.QUARTER";
                default:
                    return "EsDateInterval.YEAR";
            }
        }
        String i = expression.substring(0, length - 1);
        switch (c) {
            case 's':
                return String.format("EsDateInterval.seconds(%s)", i);
            case 'm':
                return String.format("EsDateInterval.minutes(%s)", i);
            case 'h':
                return String.format("EsDateInterval.hours(%s)", i);
            case 'd':
                return String.format("EsDateInterval.days(%s)", i);
            default:
                return String.format("EsDateInterval.weeks(%s)", i);
        }
    }
}
