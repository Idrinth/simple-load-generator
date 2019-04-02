package de.idrinth.load.validator;

public class AssertionClassNotAvaible extends Exception
{
    AssertionClassNotAvaible(String classname, Exception try1, Exception try2) {
        super ("Failed to instanciate class "+classname+" with parameter '"+try1.getMessage()+"' and without '"+try2.getMessage()+"'.");
    }
}
