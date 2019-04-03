package de.idrinth.load.validator;

import java.util.Map;

public class ContentTypeValidator implements ResponseValidator
{
    private final String mimeType;

    public ContentTypeValidator(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public void validate(String body, Map<String, String> headers) throws AssertionFailed {
        validate(headers);
    }

    @Override
    public void validate(Map<String, String> headers) throws AssertionFailed {
        if (!headers.get("content-type").contains(mimeType)) {
            throw new AssertionFailed(
                this.getClass().getName(),
                "Content Type '"+headers.get("content-type")+"' doesn't match expected "+mimeType
            );
        }
    }
}
