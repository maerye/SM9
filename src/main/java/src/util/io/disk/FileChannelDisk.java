package src.util.io.disk;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public class FileChannelDisk<S extends Sector> implements Disk<S> {

    protected List<Sector> sectors;
    protected Map<String, Sector> sectorsMap;

    protected FileChannel channel;

    public FileChannelDisk() {
        this.sectors = new ArrayList<Sector>();
        this.sectorsMap = new HashMap<String, Sector>();
    }

    public S getSectorAt(int index) {
        return (S) sectors.get(index);
    }

    public S getSector(String key) {
        return (S) sectorsMap.get(key);
    }

    public void flush() {
        if (channel != null)
            try {
                channel.force(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }


    public FileChannelDisk<S> mapTo(FileChannel channel) {
        try {
            this.channel = channel;
            int channelCursor = 0;
            for (Sector sector : sectors) {
                channelCursor += sector.mapTo(
                        Sector.Mode.READ,
                        channel.map(FileChannel.MapMode.READ_ONLY, channelCursor, sector.getLengthInBytes())
                ).getLengthInBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public FileChannelDisk<S> mapTo(String filePath) {
        int size = 0;
        for (Sector sector : sectors) {
            size += sector.getLengthInBytes();
        }

        try {
            RandomAccessFile f = new RandomAccessFile(filePath, "rw");
            f.setLength(size);
            channel = f.getChannel();

            int channelCursor = 0;
            for (Sector sector : sectors) {
                channelCursor += sector.mapTo(
                        Sector.Mode.INIT,
                        channel.map(FileChannel.MapMode.READ_WRITE, channelCursor, sector.getLengthInBytes())
                ).getLengthInBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this;
    }


    public FileChannelDisk<S> addSector(String name, S sector) {
        sectors.add(sector);
        if (name != null)
            sectorsMap.put(name, sector);

        return this;
    }

}
