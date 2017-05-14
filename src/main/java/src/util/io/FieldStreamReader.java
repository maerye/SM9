package src.util.io;


import src.api.Element;
import src.api.Field;

import java.io.DataInputStream;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class FieldStreamReader {

    private Field field;
    private byte[] buffer;
    private int offset;

    private int cursor;

    private DataInputStream dis;
    private ExByteArrayInputStream bais;


    public FieldStreamReader(Field field, byte[] buffer, int offset) {
        this.field = field;
        this.buffer = buffer;
        this.offset = offset;

        this.cursor = offset;

        this.bais = new ExByteArrayInputStream(buffer, offset, buffer.length - offset);
        this.dis = new DataInputStream(bais);
    }


    public void reset() {
        this.cursor = this.offset;
    }

    public Element readElement() {
        Element element = field.newElementFromBytes(buffer, cursor);
        jump(field.getLengthInBytes(element));
        return element;
    }

    public String readString() {
        try {
            return dis.readUTF();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cursor = bais.getPos();
        }
    }

    public int readInt() {
        try {
            return dis.readInt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cursor = bais.getPos();
        }
    }


    private void jump(int length) {
        cursor += length;
        bais.skip(length);
    }

}
