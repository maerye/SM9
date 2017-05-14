package src.field.vector;


import src.api.Element;
import src.api.ElementPowPreProcessing;
import src.api.Field;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class VectorElementPowPreProcessing implements ElementPowPreProcessing {
    protected VectorField field;
    protected ElementPowPreProcessing[] processings;

    public VectorElementPowPreProcessing(VectorElement vector) {
        this.field = vector.getField();
        this.processings = new ElementPowPreProcessing[vector.getSize()];
        for (int i = 0; i < processings.length; i++) {
            processings[i] = vector.getAt(i).getElementPowPreProcessing();
        }
    }

    public Element pow(BigInteger n) {
        List<Element> coeff = new ArrayList<Element>(processings.length);
        for (ElementPowPreProcessing processing : processings) {
            coeff.add(processing.pow(n));
        }
        return new VectorElement(field, coeff);
    }

    public Element powZn(Element n) {
        List<Element> coeff = new ArrayList<Element>(processings.length);
        for (ElementPowPreProcessing processing : processings) {
            coeff.add(processing.powZn(n));
        }
        return new VectorElement(field, coeff);
    }

    public byte[] toBytes() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public Field getField() {
        return field;
    }
}
