package de.idrinth.load.validator;

import java.lang.reflect.InvocationTargetException;

public class Factory
{
    ResponseValidator build(String name, String expectation) throws ClassNotFoundException, AssertionClassNotAvaible
    {
        var type = Class.forName(name);
        try {
            return (ResponseValidator) type.getConstructor(String.class).newInstance(expectation);
        } catch (NoSuchMethodException|InstantiationException|IllegalArgumentException|IllegalAccessException|InvocationTargetException try1) {
            try {
                return (ResponseValidator) type.getConstructor().newInstance();
            } catch (NoSuchMethodException|InstantiationException|IllegalArgumentException|IllegalAccessException|InvocationTargetException try2) {
                throw new AssertionClassNotAvaible(name, try1, try2);
            }
        }
    }
}
