package src.util.io.disk;

import java.nio.ByteBuffer;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public interface Sector {

    enum Mode {INIT, READ}


    int getLengthInBytes();

    Sector mapTo(Mode mode, ByteBuffer buffer);

}
