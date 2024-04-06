package arkuarku.toy.http;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
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
            if (request.getMethod() != ToyHttpMethod.GET && request.getMethod() != ToyHttpMethod.HEAD) {
                return Optional.empty();
            }
            String path = request.getUrl().getPath();
            if (path.endsWith("/")) {
                path += "index.html";
            }
            File file = new File(documentRoot, path);
            if (!file.canRead()) {
                return Optional.empty();
            }
            ToyHttpResponse response = new ToyHttpResponse();
            response.getHeaders().addHeader("Content-Type", Optional.ofNullable(Files.probeContentType(file.toPath())).orElse("application/octet-stream"));
            response.getHeaders().addHeader("Content-Length", String.valueOf(file.length()));
            // handle range header
            if (request.getHeaders().getHeaderFirst("Range") != null) {
                String range = request.getHeaders().getHeaderFirst("Range");
                String[] rangeParts = range.split("=");
                String[] rangeValues = rangeParts[1].split("-");
                long start = Long.parseLong(rangeValues[0]);
                long end = rangeValues.length > 1 ? Long.parseLong(rangeValues[1]) : 0;
                long length = file.length();
                if (end == 0) {
                    end = length - 1;
                }
                if (start > end) {
                    response.setStatus(416);
                    response.setStatusText("Requested Range Not Satisfiable");
                    return Optional.of(response);
                }
                if (start >= length) {
                    response.setStatus(416);
                    response.setStatusText("Requested Range Not Satisfiable");
                    return Optional.of(response);
                }
                if (end >= length) {
                    end = length - 1;
                }
                response.setStatus(206);
                response.setStatusText("Partial Content");
                response.getHeaders().addHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
                response.getHeaders().setHeader("Content-Length", String.valueOf(end - start + 1));
                if (request.getMethod() == ToyHttpMethod.GET) {
                    response.setBody(FileChannel.open(file.toPath(), StandardOpenOption.READ).position(start));
                }
                return Optional.of(response);
            }

            if (request.getMethod() == ToyHttpMethod.GET) {
                response.setBody(FileChannel.open(file.toPath(), StandardOpenOption.READ));
            }
            return Optional.of(response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while handling request", e);
            ToyHttpResponse serverError = new ToyHttpResponse();
            serverError.setStatus(500);
            serverError.setStatusText("Internal Server Error");
            return Optional.of(serverError);
        }
    }
}
