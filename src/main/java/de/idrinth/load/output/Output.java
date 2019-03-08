package de.idrinth.load.output;

import de.idrinth.load.Result;
import java.io.File;
import java.io.IOException;
import javax.xml.transform.TransformerException;

public interface Output {
    void process(File file) throws IOException;
    void process(Result result);
    void write() throws TransformerException, IOException;
}
