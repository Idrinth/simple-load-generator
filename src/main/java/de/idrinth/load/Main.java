package de.idrinth.load;

import de.idrinth.load.output.Console;
import de.idrinth.load.output.Output;
import de.idrinth.load.output.Xml;
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
            Executor executor = new Executor(outputs);
            executor.run(args);
            System.exit(0);
        } catch (ParserConfigurationException ex) {
            System.err.print(ex);
            System.exit(1);
        }
    }
}
