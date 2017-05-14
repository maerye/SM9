package src.field.gt;

import src.api.Field;
import src.field.base.AbstractFieldOver;
import src.pairing.f.map.PairingMap;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class GTFiniteField<F extends Field> extends AbstractFieldOver<F, GTFiniteElement> {
    protected PairingMap pairing;
    protected BigInteger order;


    public GTFiniteField(SecureRandom random, BigInteger order, PairingMap pairing, F targetField) {
        super(random, targetField);

        this.order = order;
        this.pairing = pairing;
    }

    
    public GTFiniteElement newElement() {
        return new GTFiniteElement(pairing, this);
    }

    public BigInteger getOrder() {
        return order;
    }

    public GTFiniteElement getNqr() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int getLengthInBytes() {
        return getTargetField().getLengthInBytes();
    }

}
