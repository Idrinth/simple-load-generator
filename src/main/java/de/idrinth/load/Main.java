package de.idrinth.load;

import de.idrinth.load.output.Console;
import de.idrinth.load.output.Csv;
import de.idrinth.load.output.Output;
import de.idrinth.load.output.Xml;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;

public class Main
{
    public static void main(String[] args) 
    {
        try {
            ArrayList<Output> outputs = new ArrayList<>();
            outputs.add(new Xml());
            outputs.add(new Console());
            outputs.add(new Csv());
            new Executor(outputs).run(args);
            System.exit(0);
        } catch (ParserConfigurationException|IOException ex) {
            System.err.print(ex);
            System.exit(1);
        }
    }
}
