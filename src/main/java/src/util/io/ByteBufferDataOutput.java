package src.util.io;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class ByteBufferDataOutput implements DataOutput {

    private ByteBuffer buffer;

    public ByteBufferDataOutput(ByteBuffer buffer) {
        this.buffer = buffer;
    }


    public void write(int b) throws IOException {
        throw new IllegalStateException();
    }

    public void write(byte[] b) throws IOException {
        buffer.put(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        throw new IllegalStateException();
    }

    public void writeBoolean(boolean v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeByte(int v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeShort(int v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeChar(int v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeInt(int v) throws IOException {
        buffer.putInt(v);
    }

    public void writeLong(long v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeFloat(float v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeDouble(double v) throws IOException {
        throw new IllegalStateException();
    }

    public void writeBytes(String s) throws IOException {
        throw new IllegalStateException();
    }

    public void writeChars(String s) throws IOException {
        throw new IllegalStateException();
    }

    public void writeUTF(String s) throws IOException {
        throw new IllegalStateException();
    }
}
