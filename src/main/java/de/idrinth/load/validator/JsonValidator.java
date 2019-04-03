package de.idrinth.load.validator;

import java.util.Map;

class JsonValidator implements ResponseValidator
{
    public JsonValidator(String schemaUrl) {
    }
    public JsonValidator() {
    }
    
    @Override
    public void validate(String body, Map<String, String> headers) throws AssertionFailed {
        
    }

    @Override
    public void validate(Map<String, String> headers) throws AssertionFailed {
        throw new AssertionFailed(this.getClass().getName(), "empty body can't be json.");
    }
    
}
