package de.idrinth.load;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

public class Main
{
    private static File[] getFiles(String[] args)
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
    private static String getSuite(File config) throws IOException
    {
        var name = config.getName();
        if (name.contains(".")) {
            name = name.substring(0, name.indexOf("."));
        }
        return name;
    }
    public static void main(String[] args)
    {
        try {
            var dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            dom.setXmlVersion("1.0");
            dom.appendChild(dom.createElement("results"));
            var formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);
            for (var file : getFiles(args)) {
                Element root = dom.createElement("suite");
                root.setAttribute("name", getSuite(file));
                root.setAttribute("date", LocalDate.now(ZoneId.of("UTC")).toString());
                root.setAttribute("time", LocalTime.now(ZoneId.of("UTC")).toString());                
                dom.getDocumentElement().appendChild(root);
                if (!file.isFile()) {
                    continue;
                }
                var it = new Runner(file).run();
                System.out.printf(
                    "\n%20s | %50s | %8s | %8s | %12s | %12s | %12s\n",
                    "Test",
                    "URL",
                    "Errors",
                    "Requests",
                    "Average(ns)",
                    "Fastest(ns)",
                    "Slowest(ns)"
                );
                for(var result : it) {
                    var res = result.get();
                    System.out.printf(
                        "%20s | %50s | %8s | %8s | %12s | %12s | %12s\n",
                        res.getName(),
                        res.getUrl(),
                        res.getErrors(),
                        res.getRequests(),
                        formatter.format(res.getAverage()),
                        formatter.format(res.getFastest()),
                        formatter.format(res.getSlowest())
                    );
                    var test = dom.createElement("test");
                    test.setAttribute("name", res.getName());
                    for(var method : res.getClass().getMethods()) {
                        if (!method.getName().equals("getClass") && !method.getName().equals("getName") && method.getName().startsWith("get") && method.getParameterCount() == 0) {
                            try {
                                var node = dom.createElement(method.getName().substring(3).toLowerCase());
                                node.appendChild(dom.createTextNode(formatForOutput(formatter, method.invoke(res))));            
                                test.appendChild(node);
                            } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
                                System.err.println(ex);
                            }
                        }
                    }
                    root.appendChild(test);
                }
            }
            StringWriter sw = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(dom), new StreamResult(sw));
            FileUtils.writeStringToFile(
                new File("results.xml"),
                sw.toString(),
                "UTF-8"
            );
        } catch (IOException|ExecutionException|InterruptedException|ParserConfigurationException|TransformerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println();
        System.exit(0);
    }
    private static String formatForOutput(NumberFormat formatter, Object data)
    {
        if (data instanceof String) {
            return (String) data;
        }
        if (data instanceof Long) {
            return formatter.format((Long) data);
        }
        if (data instanceof Integer) {
            return formatter.format((Integer) data);
        }
        return data == null ? "" : data.toString();            
    }
}
