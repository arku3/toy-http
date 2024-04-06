package arkuarku.toy.http;


import java.util.function.Function;

public final class Utils {

    public static void silentlyClose(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static void silentlyCall(Function<Void, Void> function) {
        try {
            function.apply(null);
        } catch (Exception e) {
            // ignore
        }
    }
}
