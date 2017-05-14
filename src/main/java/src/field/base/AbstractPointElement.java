package src.field.base;

import src.api.Element;
import src.api.Point;
import src.api.Vector;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractPointElement<E extends Element, F extends AbstractFieldOver> extends AbstractElement<F> implements Point<E>, Vector<E> {

    protected E  x, y;


    protected AbstractPointElement(F field) {
        super(field);
    }


    public int getSize() {
        return 2;
    }

    public E getAt(int index) {
        return (index == 0) ? x : y;
    }

    public E getX() {
        return x;
    }

    public E getY() {
        return y;
    }


    public int getLengthInBytesCompressed() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public byte[] toBytesCompressed() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int setFromBytesCompressed(byte[] source) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int setFromBytesCompressed(byte[] source, int offset) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int getLengthInBytesX() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public byte[] toBytesX() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int setFromBytesX(byte[] source) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int setFromBytesX(byte[] source, int offset) {
        throw new IllegalStateException("Not Implemented yet!");
    }

}
