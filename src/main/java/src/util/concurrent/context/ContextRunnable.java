package src.util.concurrent.context;


import src.pairing.f.parameters.MutablePairingParameters;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public abstract class ContextRunnable implements Runnable, MutablePairingParameters {

    private String name;
    private ContextExecutor executor;


    public ContextRunnable() {
    }

    public ContextRunnable(String name) {
        this.name = name;
    }


    public ContextExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ContextExecutor executor) {
        this.executor = executor;
    }

    public void putBigInteger(String key, BigInteger value) {
        executor.putBigInteger(key, value);
    }

    public void putBigIntegerAt(String key, int index, BigInteger value) {
        executor.putBigIntegerAt(key, index, value);
    }

    public void putBoolean(String key, boolean value) {
        executor.putBoolean(key, value);
    }

    public void putObject(String key, Object value) {
        executor.putObject(key, value);
    }

    public boolean containsKey(String key) {
        return executor.containsKey(key);
    }

    public String getString(String key) {
        return executor.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return executor.getString(key, defaultValue);
    }

    public int getInt(String key) {
        return executor.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return executor.getInt(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {
        return executor.getBigInteger(key);
    }

    public BigInteger getBigIntegerAt(String key, int index) {
        return executor.getBigIntegerAt(key, index);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return executor.getBigInteger(key, defaultValue);
    }

    public long getLong(String key) {
        return executor.getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return executor.getLong(key, defaultValue);
    }

    public byte[] getBytes(String key) {
        return executor.getBytes(key);
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        return executor.getBytes(key, defaultValue);
    }

    public String toString(String separator) {
        return executor.toString(separator);
    }

    public Object getObject(String key) {
        return executor.getObject(key);
    }
}
