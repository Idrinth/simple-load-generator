package de.idrinth.load.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

public class Factory {
    public static List<Output> build() throws ParserConfigurationException, IOException
    {
        ArrayList<Output> outputs = new ArrayList<>();
        outputs.add(new Xml());
        outputs.add(new Console());
        outputs.add(new Csv());
        return outputs;
    }
}
