package src.util;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class Arrays {

    public static byte[] copyOf(byte[] original, int newLength) {
        int len = Math.min(original.length, newLength);
        byte[] copy = new byte[len];
        System.arraycopy(original, 0, copy, 0, len);
        return copy;
    }

    public static byte[] copyOf(byte[] original, int offset, int newLength) {
        int len = Math.min(original.length - offset, newLength);
        byte[] copy = new byte[len];
        System.arraycopy(original, offset, copy, 0, len);
        return copy;
    }

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }

}
