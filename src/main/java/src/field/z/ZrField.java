package src.field.z;


import src.field.base.AbstractField;
import src.util.math.BigIntegerUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class ZrField extends AbstractField<ZrElement> {
    protected BigInteger order;
    protected ZrElement nqr;
    protected int fixedLengthInBytes;
    protected BigInteger twoInverse;


    public ZrField(BigInteger order) {
        this(new SecureRandom(), order, null);
    }

    public ZrField(SecureRandom random, BigInteger order) {
        this(random, order, null);
    }

    public ZrField(BigInteger order, BigInteger nqr) {
        this(new SecureRandom(), order, nqr);
    }

    public ZrField(SecureRandom random, BigInteger order, BigInteger nqr) {
        super(random);
        this.order = order;
        this.orderIsOdd = BigIntegerUtils.isOdd(order);

        this.fixedLengthInBytes = (order.bitLength() + 7) / 8;

        this.twoInverse = BigIntegerUtils.TWO.modInverse(order);

        if (nqr != null)
            this.nqr = newElement().set(nqr);
    }


    public ZrElement<ZrField> newElement() {
        return new ZrElement<ZrField>(this);
    }

    public BigInteger getOrder() {
        return order;
    }

    public ZrElement getNqr() {
        if (nqr == null) {
            nqr = newElement();
            do {
                nqr.setToRandom();
            } while (nqr.isSqr());
        }
        
        return nqr.duplicate();
    }

    public int getLengthInBytes() {
        return fixedLengthInBytes;
    }
    
}