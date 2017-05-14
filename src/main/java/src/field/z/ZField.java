package src.field.z;


import src.field.base.AbstractField;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class ZField extends AbstractField<ZElement> {

    public ZField() {
        this(new SecureRandom());
    }

    public ZField(SecureRandom random) {
        super(random);
    }


    public ZElement newElement() {
        return new ZElement(this);
    }

    public BigInteger getOrder() {
        return BigInteger.ZERO;
    }

    public ZElement getNqr() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public int getLengthInBytes() {
        return -1;
    }
    
}
