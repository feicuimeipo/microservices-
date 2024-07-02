
# Springcloud组件的适用

|Netflix	| 推荐替代品                 |说明|
|Hystrix	|Resilience4j               |Hystrix自己也推荐你使用它代替自己|
|Hystrix    |Dashboard / Turbine	    |Micrometer + Monitoring System	说白了，监控这件事交给更专业的组件去做
|Ribbon	    |Spring Cloud Loadbalancer	|忍不住了，Spring终究亲自出手|
|Zuul 1	    |Spring Cloud Gateway	    |忍不住了，Spring终究亲自出手|
|Archaius 1  |Spring Boot外部化配置 + Spring Cloud配置	|比Netflix实现的更好、更强大
