package ai.houyi.dorado.rest.util;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import static org.junit.Assert.*;

public class VirtualThreadPerTaskExecutorReflectionFactoryTest {

    @Test
    public void createShouldReturnExecutorServiceWhenMethodExists() {
        ExecutorService executorService = VirtualThreadPerTaskExecutorReflectionFactory.create("dorado-vt-worker");
        assertNotNull(executorService);
    }

    @Test
    public void createVirtualThreadFactory() throws
            InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException,
            ClassNotFoundException {
        ThreadFactory threadFactory =
                VirtualThreadPerTaskExecutorReflectionFactory.createVirtualThreadFactory("test-thread");
        assertNotNull(threadFactory);
    }
}