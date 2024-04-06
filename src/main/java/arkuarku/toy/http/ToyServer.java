package arkuarku.toy.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToyServer {
    private static final Logger logger = SimpleLogger.getLogger(ToyServer.class.getName());
    private final int port;
    private final List<ToyRequestHandler> handlers = new ArrayList<>();
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public ToyServer(int port, File documentRoot) {
        this.port = port;
        this.handlers.add(new ToyFileHandler(documentRoot));
    }

    public void start() throws IOException {
        logger.info("Server started on port " + port);
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, serverSocketChannel.validOps());

            mainLoop();
        } catch (IOException e) {
            Utils.silentlyClose(serverSocketChannel, selector);
        }

    }

    private void mainLoop() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        logger.fine("Accepting new connection");
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);
                        // Register the new channel with the selector
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        logger.fine("Start new VirtualThread to handle request");
                        Thread.startVirtualThread(new RequestHandlerRunnable(key));
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error in select", e);
            }
        }
    }

    private ToyHttpRequest createFrom(BufferedReader reader) throws IOException {
        ToyHttpHeaders headers = new ToyHttpHeaders();
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Empty request");
        }
        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + line);
        }
        ToyHttpMethod method = ToyHttpMethod.valueOf(parts[0].toUpperCase());
        String path = parts[1];
        String protocol = parts[2].toUpperCase();
        if (!protocol.equals("HTTP/1.1")) {
            throw new IOException("Unsupported protocol: " + protocol);
        }

        line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new IOException("Invalid header line: " + line);
            }
            String[] values = parts[1].split(",");
            for (String value : values) {
                headers.addHeader(parts[0], value.trim());
            }
            line = reader.readLine();
        }
        URL url = new URL(Optional.ofNullable(headers.getHeaderFirst("host")).map(host -> "http://" + host).orElse("http://127.0.0.1") + path);

        // Do not close the reader here, as it will close the underlying stream
        // TODO: read the body of the request into a buffer, if it exceeds a certain size, close the connection
        return new ToyHttpRequest(method, url, protocol, headers, reader);
    }


    private class RequestHandlerRunnable implements Runnable {
        private final SocketChannel clientChannel;
        private final SelectionKey key;

        public RequestHandlerRunnable(SelectionKey key) {
            this.key = key;
            this.clientChannel = (SocketChannel) key.channel();
            key.interestOpsAnd(~SelectionKey.OP_READ); // remove the read interest, as we are handling it
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            boolean keepAlive = false;
            try {
                logger.fine("RemoteAddress:" + clientChannel.getRemoteAddress());

                reader = new BufferedReader(Channels.newReader(clientChannel, "UTF-8")); // TODO: HTTP/1.1 header should be acsii?
                ToyHttpRequest request = createFrom(reader);
                logger.finer(request.toString());
                if (request.getHeaders().getHeaderFirst("Connection").equalsIgnoreCase("keep-alive")) {
                    keepAlive = true;
                }

                ToyHttpResponse response = handlers.stream().map(handler -> handler.handle(request)).filter(Optional::isPresent).map(Optional::get).findFirst().orElseGet(() -> {
                    ToyHttpResponse notFound = new ToyHttpResponse();
                    notFound.setStatus(404);
                    notFound.setStatusText("Not Found");
                    return notFound;
                });
                logger.finer(response.getStatus() + " " + response.getStatusText());
                logger.finer(response.getHeaders().toString());
                if (keepAlive) {
                    response.getHeaders().setHeader("Connection", "keep-alive");
                    response.getHeaders().setHeader("Keep-Alive", "timeout=5, max=100");
                }
                clientChannel.write(response.headerBuffer());
                if (response.getBody() != null) {
                    ByteBuffer body = ByteBuffer.allocate(2048);
                    while (response.getBody().read(body) != -1) {
                        body.flip();
                        clientChannel.write(body);
                        body.clear();
                    }
                }
            } catch (ClosedChannelException e) {
                logger.log(Level.FINEST, "Client connection closed", e);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error in request", e);
                keepAlive = false; // error in request, close the connection
            } finally {
                Utils.silentlyClose(reader);
                if (keepAlive) {
                    if (key.isValid()) {
                        key.interestOpsOr(SelectionKey.OP_READ); // add the read interest back
                    }
                } else {
                    Utils.silentlyClose(clientChannel);
                }
            }
        }
    }

}
