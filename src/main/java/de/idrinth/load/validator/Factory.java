package de.idrinth.load.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Factory
{
    private ResponseValidator build(String name, String expectation) throws ClassNotFoundException, AssertionClassNotAvaible
    {
        var type = Class.forName(name);
        try {
            if (null == expectation || expectation.isBlank()) {
                return (ResponseValidator) type.getConstructor().newInstance();
            }
            return (ResponseValidator) type.getConstructor(String.class).newInstance(expectation);
        } catch (NoSuchMethodException|InstantiationException|IllegalArgumentException|IllegalAccessException|InvocationTargetException exception) {
            throw new AssertionClassNotAvaible(name, exception);
        }
    }
    public List<ResponseValidator> create(Map<String, String> asserts) throws AssertionClassNotAvaible, ClassNotFoundException
    {
        var validators = new ArrayList<ResponseValidator>();
        for (var className : asserts.keySet()) {
            validators.add(build(className, String.valueOf(asserts.get(className))));
        }
        return validators;
    }
}
