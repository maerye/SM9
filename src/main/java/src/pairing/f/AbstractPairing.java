package src.pairing.f;

import src.api.*;
import src.pairing.f.map.PairingMap;

import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractPairing implements Pairing {

    protected SecureRandom random;

    protected Field G1, G2, GT, Zr;
    protected PairingMap pairingMap;


    protected AbstractPairing(SecureRandom random) {
        this.random = (random == null) ? new SecureRandom() : random;
    }

    protected AbstractPairing() {
        this(new SecureRandom());
    }


    public boolean isSymmetric() {
        return true;
    }

    public Field getG1() {
        return G1;
    }

    public Field getG2() {
        return G2;
    }

    public Field getZr() {
        return Zr;
    }

    public int getDegree() {
        return 2;
    }

    public Field getFieldAt(int index) {
        switch (index) {
            case 0:
                return Zr;
            case 1:
                return G1;
            case 2:
                return G2;
            case 3:
                return GT;
            default:
                throw new IllegalArgumentException("invalid index");
        }
    }

    public Field getGT() {
        return GT;
    }

    public Element pairing(Element in1, Element in2) {
        if (!G1.equals(in1.getField()))
            throw new IllegalArgumentException("pairing 1st input mismatch");
        if (!G2.equals(in2.getField()))
            throw new IllegalArgumentException("pairing 2nd input mismatch");

        if (in1.isZero() || in2.isZero())
            return GT.newElement().setToZero();

        return pairingMap.pairing((Point) in1, (Point) in2);
    }

    public PairingPreProcessing getPairingPreProcessingFromElement(Element in1) {
        if (!G1.equals(in1.getField()))
            throw new IllegalArgumentException("pairing 1st input mismatch");

        return pairingMap.pairing((Point) in1);
    }

    public PairingPreProcessing getPairingPreProcessingFromBytes(byte[] source) {
        return pairingMap.pairing(source, 0);
    }

    public PairingPreProcessing getPairingPreProcessingFromBytes(byte[] source, int offset) {
        return pairingMap.pairing(source, offset);
    }

    public boolean isAlmostCoddh(Element a, Element b, Element c, Element d) {
        return pairingMap.isAlmostCoddh(a, b, c, d);
    }

    public int getFieldIndex(Field field) {
        if (field == Zr)
            return 0;
        if (field == G1)
            return 1;
        if (field == G2)
            return 2;
        if (field == GT)
            return 3;

        return -1;
    }

    public boolean isProductPairingSupported() {
        return pairingMap.isProductPairingSupported();
    }

    public Element pairing(Element[] in1, Element[] in2) {
        if (in1.length != in2.length)
            throw new IllegalArgumentException("Array lengths mismatch.");

        for (int i = 0; i < in1.length; i++) {
            if (!G1.equals(in1[i].getField()))
                throw new IllegalArgumentException("pairing 1st input mismatch");
            if (!G2.equals(in2[i].getField()))
                throw new IllegalArgumentException("pairing 2nd input mismatch");

            if (in1[i].isZero() || in2[i].isZero())
                return GT.newElement().setToZero();
        }

        return pairingMap.pairing(in1, in2);
    }

    public int getPairingPreProcessingLengthInBytes() {
        return pairingMap.getPairingPreProcessingLengthInBytes();
    }


    public PairingMap getPairingMap() {
        return pairingMap;
    }

    public void setPairingMap(PairingMap pairingMap) {
        this.pairingMap = pairingMap;
    }

}
