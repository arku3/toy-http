package arkuarku.toy.http;


public final class Utils {

    public static void slientlyClose(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if(closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
