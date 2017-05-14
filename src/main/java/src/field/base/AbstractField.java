package src.field.base;



import src.api.Element;
import src.api.ElementPowPreProcessing;
import src.api.Field;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractField<E extends Element> implements Field<E> {

    protected boolean orderIsOdd = false;
    protected SecureRandom random;


    protected AbstractField(SecureRandom random) {
        this.random = random;
    }


    public E newElement(int value) {
        E e = newElement();
        e.set(value);

        return e;
    }

    public E newElement(BigInteger value) {
        E e = newElement();
        e.set(value);

        return e;
    }

    public E newElement(E e) {
        E newElement = newElement();
        newElement.set(e);

        return newElement;
    }

    public E newElementFromHash(byte[] source, int offset, int length) {
        E e = newElement();
        e.setFromHash(source, offset, length);

        return e;
    }

    public E newElementFromBytes(byte[] source) {
        E e = newElement();
        e.setFromBytes(source);

        return e;
    }

    public E newElementFromBytes(byte[] source, int offset) {
        E e = newElement();
        e.setFromBytes(source, offset);

        return e;
    }

    public E newZeroElement() {
        E e = newElement();
        e.setToZero();

        return e;
    }

    public E newOneElement() {
        E e = newElement();
        e.setToOne();

        return e;
    }

    public E newRandomElement() {
        E e = newElement();
        e.setToRandom();

        return e;
    }

    public boolean isOrderOdd() {
        return orderIsOdd;
    }

    public int getLengthInBytes(Element e) {
        return getLengthInBytes();
    }

    public int getCanonicalRepresentationLengthInBytes() {
        return getLengthInBytes();
    }

    public Element[] twice(Element[] elements) {
        for (Element element : elements) {
            element.twice();
        }

        return elements;
    }

    public Element[] add(Element[] a, Element[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i].add(b[i]);
        }

        return a;
    }

    public ElementPowPreProcessing getElementPowPreProcessingFromBytes(byte[] source) {
        return new AbstractElementPowPreProcessing(this, AbstractElementPowPreProcessing.DEFAULT_K, source, 0);
    }

    public ElementPowPreProcessing getElementPowPreProcessingFromBytes(byte[] source, int offset) {
        return new AbstractElementPowPreProcessing(this, AbstractElementPowPreProcessing.DEFAULT_K, source, offset);
    }

    public SecureRandom getRandom() {
        return random;
    }
}
