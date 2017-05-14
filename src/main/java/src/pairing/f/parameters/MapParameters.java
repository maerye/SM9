package src.pairing.f.parameters;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class MapParameters implements MutablePairingParameters {

    protected Map<String, Object> values;


    public MapParameters() {
        this.values = new LinkedHashMap<String, Object>();
    }

    public MapParameters(Map<String, Object> values) {
        this.values = values;
    }


    public String getType() {
        return (String) values.get("type");
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public int getInt(String key) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int getInt(String key, int defaultValue) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public long getLong(String key) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public long getLong(String key, long defaultValue) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public BigInteger getBigInteger(String key) {
        return (BigInteger) values.get(key);
    }

    public BigInteger getBigIntegerAt(String key, int index) {
        Object value = getObject(key);

        if (value instanceof List) {
            List list = (List) value;
            return (BigInteger) list.get(index);
        }

        if (value instanceof BigInteger[]) {
            return ((BigInteger[]) value)[index];
        }

        throw new IllegalArgumentException("Key not found or invalid");
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public String getString(String key) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public String getString(String key, String defaultValue) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public byte[] getBytes(String key) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public Object getObject(String key) {
        return values.get(key);
    }

    public String toString(String separator) {
        throw new IllegalStateException("Not Implemented yet!");
    }


    public void putObject(String key, Object value) {
        values.put(key, value);
    }

    public void putBigIntegerAt(String key, int index, BigInteger value) {
        Object obj = getObject(key);

        if (obj instanceof Map) {
            Map map = (Map) obj;
            map.put(index, value);
        } else {
            Map map = new HashMap<Integer, BigInteger>();
            map.put(index, value);

            values.put(key, map);
        }
    }

    public void putBigInteger(String key, BigInteger value) {
        values.put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        values.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapParameters that = (MapParameters) o;

        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }


}
