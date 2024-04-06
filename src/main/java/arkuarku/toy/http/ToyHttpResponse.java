package arkuarku.toy.http;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ToyHttpResponse {
    private int status = 200;
    private String statusText = "OK";
    private String protocol = "HTTP/1.1";
    private final ToyHttpHeaders headers;
    private ReadableByteChannel body;

    public ToyHttpResponse() {
        headers = new ToyHttpHeaders();
        headers.addHeader("Server", "ToyServer");
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public ToyHttpHeaders getHeaders() {
        return headers;
    }

    public void setBody(ReadableByteChannel body) {
        this.body = body;
    }

    public ReadableByteChannel getBody() {
        return body;
    }

    public ByteBuffer headerBuffer() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append(" ").append(status).append(" ").append(statusText).append("\r\n");
        sb.append(headers.toString());
        sb.append("\r\n");
        return ByteBuffer.wrap(sb.toString().getBytes());
    }

}
