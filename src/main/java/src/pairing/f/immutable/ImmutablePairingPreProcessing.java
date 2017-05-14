package src.pairing.f.immutable;


import src.api.Element;
import src.api.PairingPreProcessing;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class ImmutablePairingPreProcessing implements PairingPreProcessing {

    private PairingPreProcessing pairingPreProcessing;

    public ImmutablePairingPreProcessing(PairingPreProcessing pairingPreProcessing) {
        this.pairingPreProcessing = pairingPreProcessing;
    }

    public Element pairing(Element in2) {
        return pairingPreProcessing.pairing(in2).getImmutable();
    }

    public byte[] toBytes() {
        return pairingPreProcessing.toBytes();
    }
}
