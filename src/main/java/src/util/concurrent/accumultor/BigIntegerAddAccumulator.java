package src.util.concurrent.accumultor;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class BigIntegerAddAccumulator extends AbstractAccumulator<BigInteger> {


    public BigIntegerAddAccumulator() {
        this.result = BigInteger.ZERO;
    }


    protected void reduce(BigInteger value) {
        result = result.add(value);
    }

}
