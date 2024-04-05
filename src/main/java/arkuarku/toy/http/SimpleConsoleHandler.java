package arkuarku.toy.http;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

public class SimpleConsoleHandler extends ConsoleHandler {
    @Override
    protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
        super.setOutputStream(System.out);
    }
}
