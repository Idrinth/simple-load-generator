package de.idrinth.load.validator;

import java.util.Map;

public interface ResponseValidator
{
    public void validate(String body, Map<String, String> headers) throws AssertionFailed; 
    public void validate(Map<String, String> headers) throws AssertionFailed; 
}
