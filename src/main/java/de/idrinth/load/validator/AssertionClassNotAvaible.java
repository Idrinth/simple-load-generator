package de.idrinth.load.validator;

public class AssertionClassNotAvaible extends Exception
{
    AssertionClassNotAvaible(String classname, Exception exception) {
        super ("Failed to instanciate class "+classname+" with '"+exception.getMessage()+"'.");
    }
}
