package src.pairing.f.parameters;


import src.api.PairingParameters;
import src.util.io.Base64;

import java.io.*;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class PropertiesParameters implements PairingParameters, Externalizable {

    protected final Map<String, String> parameters;


    public PropertiesParameters() {
        this.parameters = new LinkedHashMap<String, String>();
    }


    public String getType() {
        return parameters.get("type");
    }


    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }

    public int getInt(String key) {
        String value = parameters.get(key);
        if (value == null)
            throw new IllegalArgumentException("Cannot find value for the following key : " + key);

        return Integer.parseInt(value);
    }

    public int getInt(String key, int defaultValue) {
        String value = parameters.get(key);
        if (value == null)
            return defaultValue;

        return Integer.parseInt(value);
    }

    public long getLong(String key) {
        String value = parameters.get(key);
        if (value == null)
            throw new IllegalArgumentException("Cannot find value for the following key : " + key);

        return Long.parseLong(value);
    }

    public long getLong(String key, long defaultValue) {
        String value = parameters.get(key);
        if (value == null)
            return defaultValue;

        return Long.parseLong(value);
    }

    public BigInteger getBigInteger(String key) {
        String value = parameters.get(key);
        if (value == null)
            throw new IllegalArgumentException("Cannot find value for the following key : " + key);

        return new BigInteger(value);
    }

    public BigInteger getBigIntegerAt(String key, int index) {
        return getBigInteger(key+index);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String value = parameters.get(key);
        if (value == null)
            return defaultValue;

        return new BigInteger(value);
    }

    public String getString(String key) {
        String value = parameters.get(key);
        if (value == null)
            throw new IllegalArgumentException("Cannot find value for the following key : " + key);

        return value;
    }

    public String getString(String key, String defaultValue) {
        String value = parameters.get(key);
        if (value == null)
            return defaultValue;

        return value;
    }

    public byte[] getBytes(String key) {
        try {
            return Base64.decode(getString(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        String value = parameters.get(key);
        if (value == null)
            return defaultValue;

        try {
            return Base64.decode(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject(String key) {
        return parameters.get(key);
    }

    public String toString(String separator) {
        StringBuilder buffer = new StringBuilder();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            buffer.append(entry.getKey()).append(separator).append(entry.getValue()).append("\n");
        }

        return buffer.toString();
    }


    public void put(String key, String value) {
        parameters.put(key, value);
    }

    public void putBytes(String key, byte[] bytes) {
        parameters.put(key, Base64.encodeBytes(bytes, 0, bytes.length));
    }

    public String remove(String key) {
        return parameters.remove(key);
    }

    public PropertiesParameters load(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                line = line.trim();
                if (line.length() == 0)
                    continue;
                if (line.startsWith("#"))
                    continue;

                StringTokenizer tokenizer = new StringTokenizer(line, "= :", false);
                String key = tokenizer.nextToken();
                String value = tokenizer.nextToken();

                parameters.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public PropertiesParameters load(String path) {
        InputStream inputStream;

        File file = new File(path);
        if (file.exists()) {
            try {
                inputStream = file.toURI().toURL().openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        }

        if (inputStream == null)
            throw new IllegalArgumentException("No valid resource found!");

        load(inputStream);

        try {
            inputStream.close();
        } catch (IOException e) {
            // Discard
        }

        return this;
    }


    public String toString() {
        return toString(" ");
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(toString());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        load(new ByteArrayInputStream(in.readUTF().getBytes()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertiesParameters that = (PropertiesParameters) o;

        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return parameters != null ? parameters.hashCode() : 0;
    }

}
