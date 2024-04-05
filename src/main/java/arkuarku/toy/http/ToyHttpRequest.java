package arkuarku.toy.http;

import java.io.BufferedReader;
import java.net.URL;

public class ToyHttpRequest {
    private final ToyHttpHeaders headers;
    private final ToyHttpMethod method;
    private final URL url;
    private final String protocol;
    private final BufferedReader reader;

    public ToyHttpRequest(ToyHttpMethod method, URL url, String protocol, ToyHttpHeaders headers, BufferedReader reader) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
        this.headers = headers;
        this.reader = reader;
    }

    public ToyHttpHeaders getHeaders() {
        return headers;
    }

    public ToyHttpMethod getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "ToyHttpServletRequest{" + "headers=" + headers + '}';
    }

}
