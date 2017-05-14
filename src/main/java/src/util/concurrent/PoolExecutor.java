package src.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class PoolExecutor<T> implements Pool<T> {

    protected CompletionService<T> pool;
    protected int counter;


    public PoolExecutor() {
        this(ExecutorServiceUtils.getFixedThreadPool());
    }

    public PoolExecutor(Executor executor) {
        this.pool = new ExecutorCompletionService<T>(executor);
        this.counter = 0;
    }


    public Pool<T> submit(Callable<T> callable) {
        counter++;
        pool.submit(callable);

        return this;
    }

    public Pool<T> submit(Runnable runnable) {
        counter++;
        pool.submit(runnable, null);

        return this;
    }

    public Pool<T> awaitTermination() {
        try {
            for (int i = 0; i < counter; i++)
                pool.take().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            counter = 0;
        }

        return this;
    }

}
