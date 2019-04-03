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
        System.out.print(headers);
    }
}
