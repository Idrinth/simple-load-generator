package de.idrinth.load;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class User {
    private final Map<String, String> headers;
    private final Map<String, String> replacements;

    public User(Map<String, String> headers, Map<String, String> replacements) {
        this.headers = headers == null ? new HashMap<>() : headers;
        replacements = replacements == null ? new HashMap<>() : replacements;
        this.replacements = new HashMap<>();
        for (String replacement : replacements.keySet()) {
            this.replacements.put(Pattern.quote("{%"+replacement+"%}"), replacements.get(replacement));
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }    
}
