package com.nx.boot.launch.env.resources;


import lombok.Getter;

import java.util.*;


/**
 * 命令行参数的封装
 */
public class CmdLineArgs {

    @Getter
    private final Map<String, List<String>> optionArgs = new HashMap<>();

    @Getter
    private final Map<String, String> optionArgStrings = new HashMap<>();


    private final Properties properties = new Properties();

    @Getter
    private final List<String> nonOptionArgs = new ArrayList<>();


    public void addOptionArg(String optionName,  String optionValue) {
        if (!this.optionArgs.containsKey(optionName)) {
            this.optionArgs.put(optionName, new ArrayList<>());
        }
        if (optionValue != null) {
            this.optionArgs.get(optionName).add(optionValue);
        }
    }

    public String getOptionArgsAsString(String key){
        List<String> list = optionArgs.get(key);
        if (list==null){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        list.forEach(value->{
            if (sb.length()>0) {
                sb.append(",");
            }
            sb.append(value);
        });
        return sb.toString();
    }



    /**
     * Return the set of all option arguments present on the command line.
     * 获取所有option参数的名称列表
     */
    public Set<String> getOptionNames() {
        return Collections.unmodifiableSet(this.optionArgs.keySet());
    }

    /**
     * Return whether the option with the given name was present on the command line.
     * 是否包含某个指定名称的option参数
     */
    public boolean containsOption(String optionName) {
        return this.optionArgs.containsKey(optionName);
    }

    /**
     * Return the list of values associated with the given option. null signifies
     * that the option was not present; empty list signifies that no values were associated
     * with this option.
     * 获取指定名称的option参数的值，如果命令行中没有这个option参数，返回null；
     * 因为一个option参数可能会被指定多个值，所以返回的是一个列表
     */
    public List<String> getOptionValues(String optionName) {
        return this.optionArgs.get(optionName);
    }

    /**
     * Add the given value to the list of non-option arguments.
     * 增加一个非option参数，一个非option参数可以被认为是只有value的一个参数，
     */
    public void addNonOptionArg(String value) {
        this.nonOptionArgs.add(value);
    }

    /**
     * Return the list of non-option arguments specified on the command line.
     * 返回所有非option参数
     */
    public List<String> getNonOptionArgs() {
        return Collections.unmodifiableList(this.nonOptionArgs);
    }

    public static CmdLineArgs parse(String... args) { // --priority1=program-agrs
        CmdLineArgs cmdLineArgs = new CmdLineArgs();
        for (String arg : args) {
            if (arg.startsWith("--")) { // 以--开头
                String optionText = arg.substring(2, arg.length()); //把--去掉，得到priority1=program-agrs
                String optionName;
                String optionValue = null;
                if (optionText.contains("=")) { //判断是否有=存在
                    optionName = optionText.substring(0, optionText.indexOf('=')); // 得到=之前的值，priority1
                    optionValue = optionText.substring(optionText.indexOf('=')+1, optionText.length()); //得到=之后的值，program-agrs
                }else {
                    optionName = optionText;
                }
                if (optionName.isEmpty() || (optionValue != null && optionValue.isEmpty())) {
                    throw new IllegalArgumentException("Invalid argument syntax: " + arg);
                }
                cmdLineArgs.addOptionArg(optionName, optionValue); // key: string  value: arraylist  , Map<String, List<String>> optionArgs
            }else {
                cmdLineArgs.addNonOptionArg(arg);
            }
        }
        return cmdLineArgs;
    }


    public Properties getProperties(){
        if (optionArgs==null){
            return properties;
        }
        for (String key : optionArgs.keySet()) {
            List<String> values = optionArgs.getOrDefault(key,new ArrayList<String>());
            String valueStr = collection2String(values,",");
            properties.put(key,valueStr);
        }
        return properties;
    }

    private static String collection2String(List<?> list, String appendToken) {
        StringBuffer sb = new StringBuffer();
        int len = list.size();
        int last = len - 1;
        for (int i = 0; i < len; i++) {
            sb.append(list.get(i));
            if (i != last) {
                sb.append(appendToken);
            }
        }
        return sb.toString();
    }


}
