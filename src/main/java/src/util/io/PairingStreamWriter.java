package src.util.io;


import src.api.Element;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class PairingStreamWriter {

    private ByteArrayOutputStream baos;
    private DataOutputStream dos;


    public PairingStreamWriter(int size) {
        this.baos = new ByteArrayOutputStream(size);
        this.dos = new DataOutputStream(baos);
    }


    public void write(String s) throws IOException {
        dos.writeUTF(s);
    }

    public void write(Element element) throws IOException {
        dos.write(element.toBytes());
    }

    public void writeInt(int value) throws IOException {
        dos.writeInt(value);
    }

    public void write(byte[] bytes) throws IOException {
        dos.write(bytes);
    }

    public byte[] toBytes() {
        return baos.toByteArray();
    }

}
