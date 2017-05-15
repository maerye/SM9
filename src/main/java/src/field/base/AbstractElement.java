package src.field.base;


import src.api.Element;
import src.api.ElementPowPreProcessing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractElement<F extends AbstractField> implements Element {

    protected F field;
    protected boolean immutable = false;


    public AbstractElement(F field) {
        this.field = field;
    }


    public F getField() {
        return field;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public Element getImmutable() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int getLengthInBytes() {
        return field.getLengthInBytes();
    }

    public int setFromBytes(byte[] source) {
        return setFromBytes(source, 0);
    }

    public int setFromBytes(byte[] source, int offset) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public Element pow(BigInteger n) {
        if (BigInteger.ZERO.equals(n)) {
            setToOne();
            return this;
        }

        elementPowWind(n);

        return this;
    }

    public Element powZn(Element n) {
        return pow(n.toBigInteger());
    }

    public ElementPowPreProcessing getElementPowPreProcessing() {
        return new AbstractElementPowPreProcessing(this, AbstractElementPowPreProcessing.DEFAULT_K);
    }

    public Element halve() {
        return mul(field.newElement().set(2).invert());
    }

    public Element sub(Element element) {
        add(element.duplicate().negate());
        return this;
    }

    public Element div(Element element) {
        return mul(element.duplicate().invert());
    }

    public Element mul(int z) {
        mul(field.newElement().set(z));

        return this;
    }

    public Element sqrt() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public byte[] toBytes() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public byte[] toCanonicalRepresentation() {
        return toBytes();
    }

    public Element mulZn(Element z) {
        return mul(z.toBigInteger());
    }

    public Element square() {
        return mul(this);
    }

    public Element twice() {
        return add(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Element && isEqual((Element) obj);
    }


    protected int optimalPowWindowSize(BigInteger n) {
        int expBits;

        expBits = n.bitLength();

        /* try to minimize 2^k + n/(k+1). */
        if (expBits > 9065)
            return 8;
        if (expBits > 3529)
            return 7;
        if (expBits > 1324)
            return 6;
        if (expBits > 474)
            return 5;
        if (expBits > 157)
            return 4;
        if (expBits > 47)
            return 3;
        return 2;
    }

    /**
     * Builds k-bit lookup window for base a
     *
     * @param k
     * @return
     */
    protected List<Element> buildPowWindow(int k) {
        int s;
        int lookupSize;
        List<Element> lookup;

        if (k < 1) {                /* no window */
            return null;
        }

        /* build 2^k lookup table.  lookup[i] = x^i. */
        /* TODO(-): a more careful word-finding algorithm would allow
         *       us to avoid calculating even lookup entries > 2
         */
        lookupSize = 1 << k;
        lookup = new ArrayList<Element>(lookupSize);

        lookup.add(field.newOneElement());
        for (s = 1; s < lookupSize; s++) {
            lookup.add(lookup.get(s - 1).duplicate().mul(this));
        }

        return lookup;
    }

    /**
     * left-to-right exponentiation with k-bit window.
     * NB. must have k >= 1.
     *
     * @param n
     */

    protected void elementPowWind(BigInteger n) {
        /* early abort if raising to power 0 */
        if (n.signum() == 0) {
            setToOne();
            return;
        }

        int word = 0;               /* the word to look up. 0<word<base */
        int wbits = 0;              /* # of bits so far in word. wbits<=k. */
        int k = optimalPowWindowSize(n);
        List<Element> lookup = buildPowWindow(k);
        Element result = field.newElement().setToOne();

        for (int inword = 0, s = n.bitLength() - 1; s >= 0; s--) {
            result.square();
            int bit = n.testBit(s) ? 1 : 0;

            if (inword == 0 && bit == 0)
                continue;           /* keep scanning.  note continue. */

            if (inword == 0) {          /* was scanning, just found word */
                inword = 1;             /* so, start new word */
                word = 1;
                wbits = 1;
            } else {
                word = (word << 1) + bit;
                wbits++; /* continue word */
            }

            if (wbits == k || s == 0) {
                result.mul(lookup.get(word));
                inword = 0;
            }
        }

        set(result);
    }


    protected String[] tokenize(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value,",");
        int n = tokenizer.countTokens();

        String[] tokens = new String[n];
        for (int i = 0; i < n; i++) {
            tokens[i] = tokenizer.nextToken();
        }
        return tokens;
    }
}
