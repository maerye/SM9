package src.pairing.f.parameters;


import src.api.PairingParameters;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 *
 * @since 2.0.0
 */
public interface MutablePairingParameters extends PairingParameters {

    void putObject(String key, Object value);

    void putBigIntegerAt(String key, int index, BigInteger value);

    void putBigInteger(String key, BigInteger value);

    void putBoolean(String key, boolean value);

}
