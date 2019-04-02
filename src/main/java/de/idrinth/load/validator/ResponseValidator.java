package de.idrinth.load.validator;

public interface ResponseValidator
{
    public void validate(String body, String[] headers) throws AssertionFailed; 
    public void validate(String[] headers) throws AssertionFailed; 
}
