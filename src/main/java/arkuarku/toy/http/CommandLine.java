package arkuarku.toy.http;

import java.io.File;

public class CommandLine {

    public static void main(String[] args) {
        int port = 3000;
        File documentRoot = new File("./public");
        // parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                port = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-d")) {
                documentRoot = new File(args[i + 1]);
                i++;
                if (!documentRoot.exists() || !documentRoot.isDirectory()) {
                    System.err.println("Document root does not exist or is not a directory: " + documentRoot);
                    System.exit(1);
                }
            } else if (args[i].equals("-h")) {
                System.out.println("Usage: java -jar toyhttp.jar [-p port] [-d documentRoot]");
                System.out.println("Options:");
                System.out.println("  -p port          Port to listen on (default: 3000)");
                System.out.println("  -d documentRoot  Document root directory (default: ./public)");
                System.exit(0);
            } else {
                System.err.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }
        ToyServer server = new ToyServer(port, documentRoot);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
