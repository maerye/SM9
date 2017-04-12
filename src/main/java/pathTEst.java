import java.util.StringTokenizer;

/**
 * Created by mzy on 2017/4/10.
 */
public class pathTEst {
    public static void main(String[] args) {
        String property = System.getProperty("java.library.path");
        StringTokenizer parser = new StringTokenizer(property, ";");
        while (parser.hasMoreTokens()) {
            System.err.println(parser.nextToken());
        }

    }
}
