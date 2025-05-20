package ai.houyi.dorado.rest.util;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import static org.junit.Assert.*;

public class VThreadExecutorFactoryTest {

    @Test
    public void createShouldReturnExecutorServiceWhenMethodExists() {
        ExecutorService executorService = VThreadExecutorFactory.create("dorado-vt-worker");
        assertNotNull(executorService);
    }

    @Test
    public void createVThreadFactory() throws
            InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException,
            ClassNotFoundException {
        ThreadFactory threadFactory =
                VThreadExecutorFactory.createVThreadFactory("test-thread");
        assertNotNull(threadFactory);
    }
}