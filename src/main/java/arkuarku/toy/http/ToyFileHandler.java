package arkuarku.toy.http;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToyFileHandler implements ToyRequestHandler {
    private static final Logger logger = SimpleLogger.getLogger(ToyFileHandler.class.getName());
    private File documentRoot;

    public ToyFileHandler(File documentRoot) {
        this.documentRoot = documentRoot;
    }

    @Override
    public Optional<ToyHttpResponse> handle(ToyHttpRequest request) {
        try {
            if (request.getMethod() == ToyHttpMethod.GET) {
                // TODO: Implement the file handling logic
                String path = request.getUrl().getPath();
                if (path.endsWith("/")) {
                    path += "index.html";
                }
                File file = new File(documentRoot, path);
                if (file.canRead()) {
                    ToyHttpResponse response = new ToyHttpResponse();
                    // TODO: add mime type
                    response.getHeaders().addHeader("Content-Type", "text/html");
                    response.getHeaders().addHeader("Content-Length", String.valueOf(file.length()));
                    response.setBody(FileChannel.open(file.toPath(), StandardOpenOption.READ));
                    return Optional.of(response);
                }
            } else if (request.getMethod() == ToyHttpMethod.HEAD) {
                // TODO: Implement the file handling logic
            }
            return Optional.empty();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while handling request", e);
            ToyHttpResponse serverError = new ToyHttpResponse();
            serverError.setStatus(500);
            serverError.setStatusText("Internal Server Error");
            return Optional.of(serverError);
        }
    }
}
