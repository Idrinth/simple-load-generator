package de.idrinth.load;

import de.idrinth.load.output.Factory;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

public class Main
{
    public static void main(String[] args) 
    {
        try {
            new Executor(Factory.build()).run(args);
            System.exit(0);
        } catch (ParserConfigurationException|IOException ex) {
            System.err.print(ex);
            System.exit(1);
        }
    }
}
