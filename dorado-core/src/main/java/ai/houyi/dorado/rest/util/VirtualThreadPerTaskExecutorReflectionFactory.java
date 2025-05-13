package ai.houyi.dorado.rest.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class VirtualThreadPerTaskExecutorReflectionFactory {

    public static ExecutorService create(String name) {
        try {
            return (ExecutorService) (Executors.class.getMethod("newThreadPerTaskExecutor", ThreadFactory.class)
                    .invoke(null, createVirtualThreadFactory(name)));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 ClassNotFoundException ex) {
            return null;
        }
    }

    public static ThreadFactory createVirtualThreadFactory(String name) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ClassNotFoundException {
        // 1. 获取Thread.ofVirtual()方法
        Method ofVirtual = Thread.class.getMethod("ofVirtual");

        // 2. 调用ofVirtual()获取Builder实例
        Object builder = ofVirtual.invoke(null);
        Class<?> builderClass = Class.forName("java.lang.Thread$Builder");
        // 3. 调用Builder的name()方法
        Method nameMethod = builderClass.getMethod("name", String.class);
        if (nameMethod.isAccessible()) {
            nameMethod.setAccessible(true);
        }
        Object namedBuilder = nameMethod.invoke(builder, name);
        // 4. 调用Builder的factory()方法
        Method factoryMethod = builderClass.getMethod("factory");
        return (ThreadFactory) factoryMethod.invoke(namedBuilder);
    }
}
