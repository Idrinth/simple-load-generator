package de.idrinth.load.validator;

import java.util.Map;
import java.util.regex.Pattern;

public class RegExpValidator implements ResponseValidator
{
    private final Pattern expression;

    public RegExpValidator(String expression) {
        this.expression = Pattern.compile(expression);
    }
    @Override
    public void validate(String body, Map<String, String> headers) throws AssertionFailed {
        if (!expression.matcher(body).matches()) {
            throw new AssertionFailed(this.getClass().getName(), "failed to match regexp.");
        }
    }

    @Override
    public void validate(Map<String, String> headers) throws AssertionFailed {
        throw new AssertionFailed(this.getClass().getName(), "empty body can't be match a regexp.");
    }
    
}
