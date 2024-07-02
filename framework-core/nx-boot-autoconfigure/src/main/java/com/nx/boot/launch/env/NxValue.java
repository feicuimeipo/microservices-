package com.nx.boot.launch.env;


import java.lang.annotation.*;



@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NxValue {

     String value();

}
