package de.idrinth.load.validator;

import java.util.Map;

public class StatusCodeValidator implements ResponseValidator
{
    private final String intendedCode;

    public StatusCodeValidator(String intendedCode) {
        this.intendedCode = intendedCode;
    }
    public StatusCodeValidator() {
        this("200");
    }

    @Override
    public void validate(String body, Map<String, String> headers) throws AssertionFailed {
        validate(headers);
    }

    @Override
    public void validate(Map<String, String> headers) throws AssertionFailed {
        if (!headers.get("@STATUS").equals(intendedCode)) {
            throw new AssertionFailed(
                this.getClass().getName(),
                "Status code '"+headers.get("@STATUS")+"' doesn't match expected "+intendedCode
            );
        }
    }
}
