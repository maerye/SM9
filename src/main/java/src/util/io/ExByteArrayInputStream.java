package src.util.io;

import java.io.ByteArrayInputStream;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public class ExByteArrayInputStream extends ByteArrayInputStream {

    public ExByteArrayInputStream(byte[] buf) {
        super(buf);
    }

    public ExByteArrayInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    public int getPos() {
        return pos;
    }


}
