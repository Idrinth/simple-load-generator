package de.idrinth.load;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class User {
    private final Map<String, String> headers;
    private final Map<String, String> replacements;
    private final Map<String, String> cookies;

    public User(Map<String, String> headers, Map<String, String> replacements, Map<String, String> cookies) {
        this.headers = headers == null ? new HashMap<>() : headers;
        this.cookies = cookies == null ? new HashMap<>() : cookies;
        this.replacements = new HashMap<>();
        if (replacements == null) {
            return;
        }
        replacements.keySet().forEach((replacement) -> {
            this.replacements.put(Pattern.quote("{%"+replacement+"%}"), replacements.get(replacement));
        });
    }
    public User() {
        this(null, null, null);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }  

    public Map<String, String> getCookies() {
        return cookies;
    }    
}
