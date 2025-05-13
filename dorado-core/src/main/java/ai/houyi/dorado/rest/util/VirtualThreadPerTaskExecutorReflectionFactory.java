package ai.houyi.dorado.rest.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import ai.houyi.dorado.exception.DoradoException;

public class VirtualThreadPerTaskExecutorReflectionFactory {

    public static ExecutorService create(String name) {
        try {
            ThreadFactory factory = createVirtualThreadFactory(name);
            if (factory == null) {
                throw new IllegalStateException("Failed to create virtual thread factory");
            }
            Method method = Executors.class.getMethod("newThreadPerTaskExecutor", ThreadFactory.class);
            return (ExecutorService) method.invoke(null, factory);
        } catch (Exception ex) {
            throw new DoradoException("Failed to create virtual thread executor", ex);
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
        if (builder == null) {
            throw new IllegalStateException("Thread.ofVirtual() returned null");
        }

        // 3. 获取Builder类
        Class<?> builderClass = Class.forName("java.lang.Thread$Builder");

        // 4. 调用Builder的name()方法
        Method nameMethod = builderClass.getMethod("name", String.class);
        nameMethod.setAccessible(true);
        Object namedBuilder = nameMethod.invoke(builder, name);

        // 5. 调用Builder的factory()方法
        Method factoryMethod = builderClass.getMethod("factory");
        return (ThreadFactory) factoryMethod.invoke(namedBuilder);
    }
}