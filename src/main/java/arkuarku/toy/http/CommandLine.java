package arkuarku.toy.http;

import java.io.File;

public class CommandLine {

    public static void main(String[] args) {
        int port = 3000;
        File documentRoot = new File("./public");
        ToyServer server = new ToyServer(port, documentRoot);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
