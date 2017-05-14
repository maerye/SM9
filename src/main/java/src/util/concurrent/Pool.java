package src.util.concurrent;

import java.util.concurrent.Callable;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public interface Pool<T> {

    Pool<T> submit(Callable<T> callable);

    Pool<T> submit(Runnable runnable);

    Pool<T> awaitTermination();

}
