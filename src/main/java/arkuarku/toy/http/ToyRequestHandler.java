package arkuarku.toy.http;

import java.util.Optional;

public interface ToyRequestHandler {
    Optional<ToyHttpResponse> handle(ToyHttpRequest request);

    boolean supports(ToyHttpRequest request);
}
