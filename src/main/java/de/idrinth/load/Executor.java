package de.idrinth.load;

import de.idrinth.load.output.Output;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Executor
{
    private final List<Output> outputs;

    public Executor(List<Output> outputs)
    {
        this.outputs = outputs;
    }

    private File[] getFiles(String[] args)
    {
        var list = new File[args.length == 0?1:args.length];
        if (args.length == 0) {
            list[0] = new File("tests.yml");
        }
        for (int i=0;i<args.length; i++) {
            list[i] = new File(args[i]);
        }
        return list;
    }
    public void run(String[] args)
    {
        try {
            var dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            dom.setXmlVersion("1.0");
            dom.appendChild(dom.createElement("results"));
            for (var file : getFiles(args)) {
                if (!file.isFile()) {
                    continue;
                }
                var it = new Runner(file).run();
                for (Output out : outputs) {
                    out.process(file);
                }
                for(var result : it) {
                    var res = result.get();
                    for (Output out : outputs) {
                        out.process(res);
                    }
                }
            }
            for (Output out : outputs) {
                out.write();
            }
        } catch (IOException|ExecutionException|InterruptedException|ParserConfigurationException|TransformerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
