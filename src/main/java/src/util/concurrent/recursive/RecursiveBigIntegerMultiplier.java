package src.util.concurrent.recursive;

import java.math.BigInteger;
import java.util.concurrent.RecursiveTask;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class RecursiveBigIntegerMultiplier extends RecursiveTask<BigInteger> {
    static final int SEQUENTIAL_THRESHOLD = 2;

    BigInteger[] values;
    int low;
    int high;

    public RecursiveBigIntegerMultiplier(BigInteger[] values, int lo, int hi) {
        this.values = values;
        this.low = lo;
        this.high = hi;
    }

    protected BigInteger compute() {
        if (high == low) {
            return values[low];
        }

        if (high - low < SEQUENTIAL_THRESHOLD) {
            return values[low].multiply(values[high]);
        } else {
            int mid = low + (high - low) / 2;

            RecursiveBigIntegerMultiplier left = new RecursiveBigIntegerMultiplier(values, low, mid);
            RecursiveBigIntegerMultiplier right = new RecursiveBigIntegerMultiplier(values, mid + 1, high);
            left.fork();

            BigInteger rightAns = right.compute();
            BigInteger leftAns = left.join();
            return rightAns.multiply(leftAns);
        }
    }

}

