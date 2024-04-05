package arkuarku.toy.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ToyHttpHeaders {
    private final LinkedHashMap<String, LinkedHashSet<String>> headers = new LinkedHashMap();

    /**
     * Adds a header to the headers.
     *
     * @param name  the name of the header
     * @param value the value of the header
     */
    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new LinkedHashSet<>()).add(value);
    }

    /**
     * Returns the set of values of the header with the given name.
     *
     * @param name
     * @return the set of values of the header with the given name, or null if the header is not present.
     */
    public Set<String> getHeader(String name) {
        LinkedHashSet<String> values = headers.get(name);
        if (values == null) {
            return null;
        }
        return Collections.unmodifiableSet(values);
    }

    public Set<String> keySet() {
        return headers.keySet();
    }

    /**
     * Returns the first value of the header with the given name.
     *
     * @param name
     * @return the first value of the header with the given name, or null if the header is not present.
     */
    public String getHeaderFirst(String name) {
        LinkedHashSet<String> values = headers.get(name);
        if (values == null) {
            return null;
        }
        return values.iterator().next();
    }

    public String toString() {
        StringBuilder headers = new StringBuilder();
        for (String name : this.headers.keySet()) {
            headers.append(name).append(": ");
            LinkedHashSet<String> values = this.headers.get(name);
            headers.append(String.join(", ", values));
            headers.append("\r\n");
        }
        return headers.toString();
    }
}
