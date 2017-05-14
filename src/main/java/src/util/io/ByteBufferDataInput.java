package src.util.io;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class ByteBufferDataInput implements DataInput {

    private ByteBuffer byteBuffer;

    public ByteBufferDataInput(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public void readFully(byte[] b) throws IOException {
        byteBuffer.get(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        throw new IllegalStateException();
    }

    public int skipBytes(int n) throws IOException {
        throw new IllegalStateException();
    }

    public boolean readBoolean() throws IOException {
        throw new IllegalStateException();
    }

    public byte readByte() throws IOException {
        return byteBuffer.get();
    }

    public int readUnsignedByte() throws IOException {
        throw new IllegalStateException();
    }

    public short readShort() throws IOException {
        throw new IllegalStateException();
    }

    public int readUnsignedShort() throws IOException {
        throw new IllegalStateException();
    }

    public char readChar() throws IOException {
        throw new IllegalStateException();
    }

    public int readInt() throws IOException {
        return byteBuffer.getInt();
    }

    public long readLong() throws IOException {
        return byteBuffer.getLong();
    }

    public float readFloat() throws IOException {
        throw new IllegalStateException();
    }

    public double readDouble() throws IOException {
        throw new IllegalStateException();
    }

    public String readLine() throws IOException {
        throw new IllegalStateException();
    }

    public String readUTF() throws IOException {
        throw new IllegalStateException();
    }

}
