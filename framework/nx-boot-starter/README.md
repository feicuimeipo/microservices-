

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