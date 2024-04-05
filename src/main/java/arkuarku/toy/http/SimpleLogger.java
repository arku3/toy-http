package arkuarku.toy.http;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class SimpleLogger {


    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        ConsoleHandler consoleHandler = new SimpleConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        try {
            consoleHandler.setEncoding("UTF-8");
        } catch (Exception e) {
        }
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.INFO);
        return logger;
    }
}
