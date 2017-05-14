package src.pairing.f.accumulator;


import src.api.Element;
import src.api.Pairing;
import src.api.PairingPreProcessing;
import src.util.concurrent.Pool;
import src.util.concurrent.accumultor.Accumulator;

import java.util.concurrent.Callable;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class ProductPairingAccumulator implements PairingAccumulator {

    private Pairing pairing;

    private int cursor;
    private Element[] in1, in2;
    private Element result;


    public ProductPairingAccumulator(Pairing pairing, int n) {
        this.pairing = pairing;
        this.in1 = new Element[n];
        this.in2 = new Element[n];
        this.cursor = 0;
    }


    public Accumulator<Element> accumulate(Callable<Element> callable) {
        throw new IllegalStateException("Not supported!!!");
    }

    public Accumulator<Element> awaitTermination() {
        awaitResult();

        return this;
    }

    public Element getResult() {
        return result;
    }

    public Pool<Element> submit(Callable<Element> callable) {
        throw new IllegalStateException("Not supported!!!");
    }

    public Pool<Element> submit(Runnable runnable) {
        throw new IllegalStateException("Not supported!!!");
    }



    public PairingAccumulator addPairing(Element e1, Element e2) {
        in1[cursor] =  e1;
        in2[cursor++] =  e2;

        return this;
    }

    public PairingAccumulator addPairing(PairingPreProcessing pairingPreProcessing, Element e2) {
        throw new IllegalStateException("Not supported!!!");
    }

    public PairingAccumulator addPairingInverse(Element e1, Element e2) {
        throw new IllegalStateException("Not supported!!!");
    }

    public Element awaitResult(){
        return (result = pairing.pairing(in1, in2));
    }

}
