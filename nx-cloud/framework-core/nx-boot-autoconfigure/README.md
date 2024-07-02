

# ServiceLoader与 SPI设计模式
SPI(Service Provider Interface),SPI技术就是可以根据某个接口找到其实现类，然后根据不同的业务场景使用不同的实现类。
ServiceLoader则是java提供的一个api, 主要是用来读取解析而这个key - list 的映射关系的

> https://www.baeldung.com/google-autoservice

1. 定义接口
```aidl
public interface IFunction {
    String getName();
    void doFunction();
}
1
```

2. 实现方法一
```aidl
@AutoService(IFunction.class)
public class FunctionA implements IFunction {
    @Override
    public String getName() {
        return "FunctionA";
    }

    @Override
    public void doFunction() {
        
    }
}
```
> 编译时会在META-INF.services下生成 注解中的class全路径命名的文件，文件内部有其实现类

3. 消费
```aidl
ServiceLoader<IFunction> load = ServiceLoader.load(IFunction.class);
for (IFunction item : load) {
    Log.i(TAG, "onCreate: " + next.getName());
}
```

# SPI和API的区别
API（Application Programming  Interface）：由于实现方完成接口实现

SPI（Service Provider Interface）：由调用方实现接口

常见例子：插件模式开发

数据库驱动 Driver
日志 Log
dubbo扩展点开发


# 启动过程
- ApplicationStartingEvent：开始启动中
@since 1.5.0，并非1.0.0就有的哦。不过现在几乎没有人用1.5以下的版本了，所以可当它是标准事件。

- ApplicationEnvironmentPreparedEvent：环境已准备好-
@since 1.0.0。该事件节点是最为重要的一个节点之一，因为对于Spring应用来说，环境抽象Enviroment简直太重要了，它是最为基础的元数据，决定着程序的构建和走向，所以构建的时机是比较早的。
- ApplicationContextInitializedEvent：上下文已实例化
@since 2.1.0，非常新的一个事件。当SpringApplication的上下文ApplicationContext准备好后，对单例Bean们实例化之前，发送此事件。所以此事件又可称为：contextPrepared事件。
- ApplicationPreparedEvent：上下文已准备好
@since 1.0.0。截止到上个事件ApplicationContextInitializedEvent，应用上下文ApplicationContext充其量叫实例化好了，但是还剩下很重要的事没做，这便是本周期的内容。
- ApplicationStartedEvent：应用成功启动
@since 2.0.0。截止到此，应用已经准备就绪，并且通过监听器、初始化器等完成了非常多的工作了，但仍旧剩下被认为最为重要的初始化单例Bean动作还没做、web容器（如Tomcat）还没启动，这便是这个周期所要做的事。
- ApplicationReadyEvent：应用已准备好
@since 1.3.0。该事件所处的生命周期可认为基本同ApplicationStartedEvent，仅是在其后执行而已，两者中间并无其它特别的动作，但是监听此事件的监听器们还是蛮重要的。

- 异常情况
 SpringApplication是有可能在启动的时候失败（如端口号已被占用），当然任何一步骤遇到异常时交给SpringApplication#handleRunFailure()方法来处理，这时候也会有对应的事件发出。

-  ApplicationFailedEvent：应用启动失败
当SpringApplication在启动时抛出异常：可能是端口绑定、也可能是你自定义的监听器你写了个bug等，就会“可能”发送此事件。
