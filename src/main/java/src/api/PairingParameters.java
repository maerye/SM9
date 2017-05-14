package src.api;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Represents the set of parameters describing a pairing.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public interface PairingParameters extends Serializable {

    /**
     * Returns <tt>true</tt> if a mapping for the specified
     * key exists.
     *
     * @param key key whose presence is to be tested
     * @return <tt>true</tt> if a mapping for the specified key exists.
     * @since 2.0.0
     */
    boolean containsKey(String key);

    /**
     * Returns the value as a string to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as a string to which the specified key is mapped.
     * @throws IllegalArgumentException if the specified key does not exists.
     * @since 2.0.0
     */
    String getString(String key);

    /**
     * Returns the value as a string to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as a string to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     * @since 2.0.0
     */
    String getString(String key, String defaultValue);

    /**
     * Returns the value as an int to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as an int to which the specified key is mapped.
     * @throws IllegalArgumentException if the specified key does not exists.
     * @since 2.0.0
     */
    int getInt(String key);

    /**
     * Returns the value as an int to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as an int to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     * @since 2.0.0
     */
    int getInt(String key, int defaultValue);

    /**
     * Returns the value as a BigInteger to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as a BigInteger to which the specified key is mapped.
     * @throws IllegalArgumentException if the specified key does not exists.
     * @since 2.0.0
     */
    BigInteger getBigInteger(String key);

    /**
     * Returns the BigInteger to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     *
     * @param key the key whose associated value is to be returned
     * @return the BigInteger to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     * @since 2.0.0
     */
    BigInteger getBigInteger(String key, BigInteger defaultValue);

    /**
     * Returns the BigInteger at the specified index in the array to which
     * the specified key is mapped.
     *
     * @param key the key whose associated array is to be used
     * @param index the index relative to the array.
     * @return the BigInteger at the specified index in the array to which
     * the specified key is mapped.
     * @since 2.0.0
     */
    BigInteger getBigIntegerAt(String key, int index);

    /**
     * Returns the value as a long to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as a long to which the specified key is mapped.
     * @throws IllegalArgumentException if the specified key does not exists.
     * @since 2.0.0
     */
    long getLong(String key);

    /**
     * Returns the value as a long to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as a long to which the specified key is mapped.
     * If the mapping does not exist the passed defaultValue is returned.
     * @since 2.0.0
     */
    long getLong(String key, long defaultValue);

    /**
     * Returns the value as an array of bytes to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as an array of bytes to which the specified key is mapped.
     * @throws IllegalArgumentException if the specified key does not exists.
     * @since 2.0.0
     */
    byte[] getBytes(String key);

    /**
     * Returns the value as an array of bytes to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value as an array of bytes to which the specified key is mapped.
     * @throws IllegalArgumentException if the specified key does not exists.
     * @since 2.0.0
     */
    byte[] getBytes(String key, byte[] defaultValue);

    /**
     * Returns a string representation of the parameters
     * using the specified key/value separator.
     *
     * @param separator key/value separator separator to be used .
     * @return a string representation of the parameters.
     * @since 2.0.0
     */
    String toString(String separator);

    /**
     * Returns the object to which the specified key is mapped.
     *
     * @param key the key whose associated object is to be returned
     * @since 2.0.0
     */
    Object getObject(String key);
}
